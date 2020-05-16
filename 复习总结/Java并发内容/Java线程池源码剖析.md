## 前言

之前自己一直学不好线程池,或者说无法深入学习这部分的内容,很大程度上是由于总是停留在如何用的阶段,而线程池这部分内容实际上阅读源码真的能够带来很大提升。

由于线程池这部分内容隶属于`  J.U.C`包下的内容，所以代码风格是典型的Doug Lea,如果你之前阅读过JUC包下其他源码的部分,对这部分源码阅读起来会更容易理解一些。

注意 : 本文将从源码部分进行阐述,在JDK1.7和1.8中,线程池部分内容变化很小,本文基于JDK1.8版本。

-------------------

## 接口结构及概览

JUC下线程池的接口结构设计的非常简洁清晰,每个接口的设计目的明确,这点上来说,在源码阅读的时候为我们提供了很大的便利。

![](https://s1.ax1x.com/2020/04/18/Je3X01.png)

这里只给出了到`ThreadPoolExecutor`的继承结构,而再往下的实现例如`  newFixedThreadPool`、`  newCacheThreadPool`这类通过`  Executors`工具类创建的线程池,实际上是对于`  ThreadPoolExcutor`的一个增强自动配置。在阿里的手册中有提示,尽量使用`  ThreadPoolExecutor`来手动配置各项参数以满足要求,防止出现内存溢出等情况。

所以,本文的重点将放在以上这四个接口和类中,从源码角度看下线程池究竟如何实现并提交任务。

### Executor

`  Excutor`是整个线程池最顶层的接口,也是整个线程池最关键的部分——任务执行。

该接口的设计非常简单,只有一个`  execute`方法,之后所有的线程池中的方法都是实现自该接口的`  executor`方法。

```java
public interface Executor {
    void execute(Runnable command);
}
```

### ExecuotrService

该接口主要是对于`  Executor`的一个增强,一般来说创建线程池都会使用这个方法作为返回值,其内部提供的方法已经能够满足大部分的需求。

像`  shutDown`,`  shutDownNow`,`  isTerminated`等方法都是和线程池生命状态有关的方法,而三个`  submit`方法的重载都是为了提交任务而设计的。最后的`  invokeAll`,`  invokeAny`方法都是作用于阻塞队列的方法,但空余线程有能力处理任务时,会调用该方法来加载阻塞队列中的任务。

`  ExecutorService`接口主要为我们提供了一系列的规则,让之后所有的线程池行为都按照这些规则来运行,例如任务如何提交,阻塞队列如何唤醒,shutDown线程时如何处理剩余任务,都会在之后的具体实现类中进行详细的规范。

### AbstractExecutorService

`  AbstractExecutorService`该抽象类提供了很多供子类直接使用的方法,例如`newTask`,由于`  Runnable`没有返回值,而线程池则要求每个任务都需要有一个返回值表示结果,所以就在这一层进行了结果的封装,类似的操作还有很多,这一层实现的基本上就是一些具有共性,所有线程池都能够使用到的操作,而这些操作也在之后的过程中扮演着非常重要的角色,具体内容在后面进行分析。

### ThreadPoolExecutor

主角登场。

到目前为止这是我们看到的第一个能够真正实现了线程池完整功能的实现类,JDK1.8中源码大约2000行,阅读起来有些繁琐,但是很清晰。而该类中又使用了很多关于Blocking Queue的相关知识,这里仅仅做个简单的介绍。

因此,除了以上四个继承结构,还需要简单了解一下这几个在线程池中出现频率非常高的结构。

![](https://s1.ax1x.com/2020/04/18/JeJg6e.png)

**Block Queue**

当线程池中没有足够的线程能够处理任务时,任务将被暂时存放于阻塞队列中等待被线程消费。实际上阻塞队列就是一个生产者消费者的模型,同样提供了`  poll`,`  peek`,`  remove`,`  add`等方法,由于在线程池中,并不会对阻塞队列有太多的源码级的操作,所以这里不对阻塞队列进行详细的描述,仅仅介绍一下概念。

- ArrayBlockingQueue : 基于数组的FIFO阻塞队列,初始化时必须指定其初始容量`  capacity`,有界队列
- LinkedBlockingQueue : 基于链表结构的无界队列(实际上有界,是Integer.MAX_VALUE),同样按照FIFO操作
- SynchronousBlockingQueue : 容量为0的队列,实际上是一个生产者直接将任务交到消费者手上,而非中间维护了一个真实存在的队列。
- PriorityBlockingQueue : 排序队列,对了不允许插入null元素,所有的元素必须实现了`  comparable`接口,是一种能够排序的无界队列。

以上就是四种常用的阻塞队列,而在实际中主要使用的就是前两种基于数组和链表结构的队列。

**Future**

由于线程池支持获取线程执行的结果,所以引入了Future接口,而`RunnableFuture`就是继承自该接口,当然,我们最应该关注的就是它的实现类`  FutureTask`,实际上理解起来很简单,在线程池使用过程中,我们向其中提交的是任务(task)，而具体表现形式可能就是一个Runnable的具体实现,但是由于Runnable并没有自己的返回值,所以需要线程池帮我们封装好一个值作为返回参数。所以在提交task的时候,会将一个Runnable和一个值包装成为一个`  FutrurTask`再提交给线程池进行处理,然后才能在将来的执行过程中获取到结果。

说了一些题外话,回到`  ThreadPoolExecutor`,这个类中主要就是实现了对于阻塞队列的操作,线程任务的包装,返回值的处理等内容,以及定义了线程池的各种状态量和逻辑阶段,在之后源码分析中会逐步揭开。

下面进入正题,开始源码的分析。

--------------------------------------------------

## Executor

虽然这个接口中只有一个方法,但是很值得我们去思考一下。我们在没有线程池的时候是如何启动一个线程来执行一个任务。

```JAVA
new Thread(new Runnable() {
    @Override
    public void run() {
        doSomething();
    }
}).start();
```

而使用线程池后呢?

```java
Executor executor = new ThreadPoolExecutor(...);//参数省略
executor.execute(new Runnable() {
    //doSomething
});
```

` Executor`是一个非常灵活的接口,之前我也认为这个接口设计的太过于简单,或者说本应该将其和`  ExecutorService`接口组合到一起作为一个顶级接口,但是看到Github的一个issue中提及了使用两个`  Executor`来实现一些操作,才发现这个接口的用处。

```java
class SerialExecutor implements Executor {
    // 任务队列
    final Queue<Runnable> tasks = new ArrayDeque<Runnable>();
    // 这个才是真正的执行器
    final Executor executor;
    // 当前正在执行的任务
    Runnable active;

    // 初始化的时候，指定执行器
    SerialExecutor(Executor executor) {
        this.executor = executor;
    }

    // 添加任务到线程池: 将任务添加到任务队列，scheduleNext 触发执行器去任务队列取任务
    public synchronized void execute(final Runnable r) {
        tasks.offer(new Runnable() {
            public void run() {
                try {
                    r.run();
                } finally {
                    scheduleNext();
                }
            }
        });
        if (active == null) {
            scheduleNext();
        }
    }

    protected synchronized void scheduleNext() {
        if ((active = tasks.poll()) != null) {
            // 具体的执行转给真正的执行器 executor
            executor.execute(active);
        }
    }
}
```

## ExecutorService

ExecutorService补充了更多的方法来满足对于线程池的操作,该接口也是我们使用最频繁的接口。

```java
ExecutorService executor = new ThreadPoolExecutor(...);
```

该接口中一系列的方法基本上满足了大部分线程池的需求,下面来看看有哪些方法。

```java
public interface ExecutorService extends Executor {

    // 关闭线程池，已提交的任务继续执行，不接受继续提交新任务
    void shutdown();

    // 关闭线程池，尝试停止正在执行的所有任务，不接受继续提交新任务
    // 它和前面的方法相比，加了一个单词“now”，区别在于它会去停止当前正在进行的任务
    List<Runnable> shutdownNow();

    // 线程池是否已关闭
    boolean isShutdown();

    // 如果调用了 shutdown() 或 shutdownNow() 方法后，所有任务结束了，那么返回true
    // 这个方法必须在调用shutdown或shutdownNow方法之后调用才会返回true
    boolean isTerminated();

    // 等待所有任务完成，并设置超时时间
    // 我们这么理解，实际应用中是，先调用 shutdown 或 shutdownNow，
    // 然后再调这个方法等待所有的线程真正地完成，返回值意味着有没有超时
    boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException;

    // 提交一个 Callable 任务
    <T> Future<T> submit(Callable<T> task);

    // 提交一个 Runnable 任务，第二个参数将会放到 Future 中，作为返回值，
    // 因为 Runnable 的 run 方法本身并不返回任何东西
    <T> Future<T> submit(Runnable task, T result);

    // 提交一个 Runnable 任务
    Future<?> submit(Runnable task);

    // 执行所有任务，返回 Future 类型的一个 list
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
            throws InterruptedException;

    // 也是执行所有任务，但是这里设置了超时时间
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                  long timeout, TimeUnit unit)
            throws InterruptedException;

    // 只有其中的一个任务结束了，就可以返回，返回执行完的那个任务的结果
    <T> T invokeAny(Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException;

    // 同上一个方法，只有其中的一个任务结束了，就可以返回，返回执行完的那个任务的结果，
    // 不过这个带超时，超过指定的时间，抛出 TimeoutException 异常
    <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                    long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException;
}
```

在`  submit`方法中提到了Future,所以在往下看实现类之前,还是有必要说一下关于Futrue的事。

### FutureTask

![](https://s1.ax1x.com/2020/04/18/JeaBR0.png)

最顶层的`  FunctionalInterface`接口不用理会,主要看其他的几个接口。

众所周知,`  Runnable`是一个没有返回值的接口,所以我们需要将其包装为一个具有返回值的接口,而这个接口就是`  RunnableFuture`,而FutrurTask又通过实现了RunnableFuture间接实现了Runnable,此时就能够`  executor.execute(Runnable())`将一个task进行传递。

而这个封装的过程实际上很简单,但是在源码中跳转了很多个类,而最终实现是如下代码。

```java
static final class RunnableAdapter<T> implements Callable<T> {
    final Runnable task;
    final T result;
    RunnableAdapter(Runnable task, T result) {
        this.task = task;
        this.result = result;
    }
    public T call() {
        task.run();
        return result;
    }
}
```

实际上就是将`  Runnable`和一个value封装为一个`  Callable`,然后在call方法中,执行run并且返回这个value。

更多关于FutureTask的内容这里就不再过多介绍,有了这些基础就能够理解下面线程池源码中的思路。

## AbstractExecutorService

AbstractExecutorService 抽象类派生自 ExecutorService 接口，然后在其基础上实现了几个实用的方法，这些方法提供给子类进行调用。

这个抽象类实现了 `invokeAny` 方法和 `invokeAll` 方法，这里的两个 `newTaskFor` 方法也比较有用，用于将任务包装成 FutureTask。定义于最上层接口 Executor中的 `void execute(Runnable command)` 由于不需要获取结果，不会进行 FutureTask 的包装,而类中invoke这类方法比较占用篇幅，但是使用较少,如果不愿花费太多时间可以跳过,这部分内容并不具有承前启后的作用。

### newTaskFor

这个方法是用来对Runnable进行封装的操作,而这个操作调用链比较长,这里直接给出,以便整理思路。

```java
protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
    return new FutureTask<T>(runnable, value);
}
//调用链路
newTaskFor(Runnable runnable, T value) -> Executors.callable(runnable, result) -> 
    callable(Runnable task, T result) -> new RunnableAdapter<T>(task, result)
```

可以看到,最后的`  new RunnableAdapter<T>(task, result)`这个返回值就是之前在讲解FutureTask封装那部分的一个实例。

### submit

`submit`方法和`execute`最大的区别在于具有返回值,会在提交一个task的时候进行封装

```java
public Future<?> submit(Runnable task) {
    if (task == null) throw new NullPointerException();
    //封装返回结果
    RunnableFuture<Void> ftask = newTaskFor(task, null);
    execute(ftask);
    return ftask;
}

public <T> Future<T> submit(Runnable task, T result) {
    if (task == null) throw new NullPointerException();
    RunnableFuture<T> ftask = newTaskFor(task, result);
    execute(ftask);
    return ftask;
}
```

### invoke

`invokeAll` 和 `invokeAny`方法内容比较多,需要仔细阅读,这里借鉴github上的一段源码分析,非常细致清晰。

```java
// 此方法目的：将 tasks 集合中的任务提交到线程池执行，任意一个线程执行完后就可以结束了
// 第二个参数 timed 代表是否设置超时机制，超时时间为第三个参数，
// 如果 timed 为 true，同时超时了还没有一个线程返回结果，那么抛出 TimeoutException 异常
private <T> T doInvokeAny(Collection<? extends Callable<T>> tasks,
                          boolean timed, long nanos)
    throws InterruptedException, ExecutionException, TimeoutException {
    if (tasks == null)
        throw new NullPointerException();
    // 任务数
    int ntasks = tasks.size();
    if (ntasks == 0)
        throw new IllegalArgumentException();
    // 
    List<Future<T>> futures= new ArrayList<Future<T>>(ntasks);

    // ExecutorCompletionService 不是一个真正的执行器，参数 this 才是真正的执行器
    // 它对执行器进行了包装，每个任务结束后，将结果保存到内部的一个 completionQueue 队列中
    // 这也是为什么这个类的名字里面有个 Completion 的原因吧。
    ExecutorCompletionService<T> ecs =
        new ExecutorCompletionService<T>(this);
    try {
        // 用于保存异常信息，此方法如果没有得到任何有效的结果，那么我们可以抛出最后得到的一个异常
        ExecutionException ee = null;
        long lastTime = timed ? System.nanoTime() : 0;
        Iterator<? extends Callable<T>> it = tasks.iterator();

        // 首先先提交一个任务，后面的任务到下面的 for 循环一个个提交
        futures.add(ecs.submit(it.next()));
        // 提交了一个任务，所以任务数量减 1
        --ntasks;
        // 正在执行的任务数(提交的时候 +1，任务结束的时候 -1)
        int active = 1;

        for (;;) {
            // ecs 上面说了，其内部有一个 completionQueue 用于保存执行完成的结果
            // BlockingQueue 的 poll 方法不阻塞，返回 null 代表队列为空
            Future<T> f = ecs.poll();
            // 为 null，说明刚刚提交的第一个线程还没有执行完成
            // 在前面先提交一个任务，加上这里做一次检查，也是为了提高性能
            if (f == null) {
                if (ntasks > 0) {
                    --ntasks;
                    futures.add(ecs.submit(it.next()));
                    ++active;
                }
                // 这里是 else if，不是 if。这里说明，没有任务了，同时 active 为 0 说明
                // 任务都执行完成了。其实我也没理解为什么这里做一次 break？
                // 因为我认为 active 为 0 的情况，必然从下面的 f.get() 返回了

                // 2018-02-23 感谢读者 newmicro 的 comment，
                //  这里的 active == 0，说明所有的任务都执行失败，那么这里是 for 循环出口
                else if (active == 0)
                    break;
                // 这里也是 else if。这里说的是，没有任务了，但是设置了超时时间，这里检测是否超时
                else if (timed) {
                    // 带等待的 poll 方法
                    f = ecs.poll(nanos, TimeUnit.NANOSECONDS);
                    // 如果已经超时，抛出 TimeoutException 异常，这整个方法就结束了
                    if (f == null)
                        throw new TimeoutException();
                    long now = System.nanoTime();
                    nanos -= now - lastTime;
                    lastTime = now;
                }
                // 这里是 else。说明，没有任务需要提交，但是池中的任务没有完成，还没有超时(如果设置了超时)
                // take() 方法会阻塞，直到有元素返回，说明有任务结束了
                else
                    f = ecs.take();
            }
            /*
                 * 我感觉上面这一段并不是很好理解，这里简单说下。
                 * 1\. 首先，这在一个 for 循环中，我们设想每一个任务都没那么快结束，
                 *     那么，每一次都会进到第一个分支，进行提交任务，直到将所有的任务都提交了
                 * 2\. 任务都提交完成后，如果设置了超时，那么 for 循环其实进入了“一直检测是否超时”
                       这件事情上
                 * 3\. 如果没有设置超时机制，那么不必要检测超时，那就会阻塞在 ecs.take() 方法上，
                       等待获取第一个执行结果
                 * 4\. 如果所有的任务都执行失败，也就是说 future 都返回了，
                       但是 f.get() 抛出异常，那么从 active == 0 分支出去(感谢 newmicro 提出)
                         // 当然，这个需要看下面的 if 分支。
                 */

            // 有任务结束了
            if (f != null) {
                --active;
                try {
                    // 返回执行结果，如果有异常，都包装成 ExecutionException
                    return f.get();
                } catch (ExecutionException eex) {
                    ee = eex;
                } catch (RuntimeException rex) {
                    ee = new ExecutionException(rex);
                }
            }
        }// 注意看 for 循环的范围，一直到这里

        if (ee == null)
            ee = new ExecutionException();
        throw ee;

    } finally {
        // 方法退出之前，取消其他的任务
        for (Future<T> f : futures)
            f.cancel(true);
    }
}

// 执行所有的任务，返回任务结果。
// 先不要看这个方法，我们先想想，其实我们自己提交任务到线程池，也是想要线程池执行所有的任务
// 只不过，我们是每次 submit 一个任务，这里以一个集合作为参数提交
public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
    throws InterruptedException {
    if (tasks == null)
        throw new NullPointerException();
    List<Future<T>> futures = new ArrayList<Future<T>>(tasks.size());
    boolean done = false;
    try {
        // 这个很简单
        for (Callable<T> t : tasks) {
            // 包装成 FutureTask
            RunnableFuture<T> f = newTaskFor(t);
            futures.add(f);
            // 提交任务
            execute(f);
        }
        for (Future<T> f : futures) {
            if (!f.isDone()) {
                try {
                    // 这是一个阻塞方法，直到获取到值，或抛出了异常
                    // 这里有个小细节，其实 get 方法签名上是会抛出 InterruptedException 的
                    // 可是这里没有进行处理，而是抛给外层去了。此异常发生于还没执行完的任务被取消了
                    f.get();
                } catch (CancellationException ignore) {
                } catch (ExecutionException ignore) {
                }
            }
        }
        done = true;
        // 这个方法返回，不像其他的场景，返回 List<Future>，其实执行结果还没出来
        // 这个方法返回是真正的返回，任务都结束了
        return futures;
    } finally {
        // 为什么要这个？就是上面说的有异常的情况
        if (!done)
            for (Future<T> f : futures)
                f.cancel(true);
    }
}
```

到这里，我们发现，这个抽象类包装了一些基本的方法，可是像 submit、invokeAny、invokeAll 等方法，它们都没有真正开启线程来执行任务，它们都只是在方法内部调用了 execute 方法，所以最重要的 execute(Runnable runnable) 方法还没出现，需要等具体执行器来实现这个最重要的部分，这里我们要说的就是 ThreadPoolExecutor 类了。

---------------------------------------

## ThreadPoolExecutor

ThreadPoolExecutor是JUC下的线程池的真正实现,在了解这个线程池的具体内容前,首先要理解一个模型——线程池消费者生产者模型。

![](https://s1.ax1x.com/2020/04/18/Jeg3DI.png)

这是最简单的模型,没有考虑队列界限,线程池大小等情况,而在实际开发中,可以通过`  Executors`创建或者直接创建一个`  ThreadPoolExecutor`对象,而这两种方法都导向了一个构造函数。

```java
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          RejectedExecutionHandler handler) {
    this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
         Executors.defaultThreadFactory(), handler);
}
```

### 构造方法

该构造方法是整个类的核心,也是面试中经常问到的方法,首先来解释下这些参数。

- corePoolSize : 核心线程数,可以理解为一般情况下常驻线程数量
- maximumPoolSize : 最大线程,线程池内不能超过此线程,超过后任务放入阻塞队列
- keepAliveTime : 线程存活时间,如果在一段时间内线程处于闲置状态,会进行销毁,最终这个数量维持在corePoolSize
- unit : 存活时间单位
- workQueue : 阻塞队列,前面介绍的四种Block Queue
- threadFactory : 线程工厂,能够生产线程,一般使用默认即可,我们可以让每个线程更具有可读性,例如`  Thread-Message-1`,代表消息发送线程1，这个操作可以在线程工厂中完成。
- handler : 拒绝策略,当线程池内没有空余线程执行任务,并且阻塞队列已满时,就需要将任务拒绝
  1. 直接抛出RejectThreadException异常
  2. 直接抛弃,不执行任何方法
  3. 抛弃一个队列中等待时间最长的任务(队头),将新任务加入队列
  4. 让用户线程来执行任务,即谁提交,谁执行。

实际上线程工厂和拒绝策略都可以自己实现,通过实现对应接口和方法即可,不过一般来说默认的几种方式都能够满足需求。

### 线程池状态

之前在其他地方看到过线程池状态的描述,无非就是running,shutDown,shutDownNow等等,但是实际上源码中这些状态的设计还是值得研究的,而Doug Lea使用了一个32位的Integer来记录线程状态和线程数量,至于为何这样做,我暂时没有想到好的解释。

这32位数据中,高3位表示线程池的生命状态,而低29位则表示线程的数量(足够使用,将近5亿线程)

```java
private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));

// 这里 COUNT_BITS 设置为 29(32-3)，意味着前三位用于存放线程状态，后29位用于存放线程数
// 很多初学者很喜欢在自己的代码中写很多 29 这种数字，或者某个特殊的字符串，然后分布在各个地方，这是非常糟糕的
private static final int COUNT_BITS = Integer.SIZE - 3;

// 000 11111111111111111111111111111
// 这里得到的是 29 个 1，也就是说线程池的最大线程数是 2^29-1=536870911
// 以我们现在计算机的实际情况，这个数量还是够用的
private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

// 我们说了，线程池的状态存放在高 3 位中
// 运算结果为 111跟29个0：111 00000000000000000000000000000
private static final int RUNNING    = -1 << COUNT_BITS;
// 000 00000000000000000000000000000
private static final int SHUTDOWN   =  0 << COUNT_BITS;
// 001 00000000000000000000000000000
private static final int STOP       =  1 << COUNT_BITS;
// 010 00000000000000000000000000000
private static final int TIDYING    =  2 << COUNT_BITS;
// 011 00000000000000000000000000000
private static final int TERMINATED =  3 << COUNT_BITS;

// 将整数 c 的低 29 位修改为 0，就得到了线程池的状态
private static int runStateOf(int c)     { return c & ~CAPACITY; }
// 将整数 c 的高 3 为修改为 0，就得到了线程池中的线程数
private static int workerCountOf(int c)  { return c & CAPACITY; }
```

上面内容就是通过位操作来获取更新线程池内容,我们更应该关注的重点在线程池这几个生命状态分别代表了什么,以及相互的转换过程。

- RUNNING：这个没什么好说的，这是最正常的状态：接受新的任务，处理等待队列中的任务
- SHUTDOWN：不接受新的任务提交，但是会继续处理等待队列中的任务
- STOP：不接受新的任务提交，不再处理等待队列中的任务，中断正在执行任务的线程
- TIDYING：所有的任务都销毁了，workCount 为 0。线程池的状态在转换为 TIDYING 状态时，会执行钩子方法 terminated()
- TERMINATED：terminated() 方法结束后，线程池的状态就会变成这个

> RUNNING 定义为 -1，SHUTDOWN 定义为 0，其他的都比 0 大，所以等于 0 的时候不能提交任务，大于 0 的话，连正在执行的任务也需要中断

这几种状态转换如下 : 

- RUNNING -> SHUTDOWN：当调用了 shutdown() 后，会发生这个状态转换，这也是最重要的
- (RUNNING or SHUTDOWN) -> STOP：当调用 shutdownNow() 后，会发生这个状态转换，这下要清楚 shutDown() 和 shutDownNow() 的区别了
- SHUTDOWN -> TIDYING：当任务队列和线程池都清空后，会由 SHUTDOWN 转换为 TIDYING
- STOP -> TIDYING：当任务队列清空后，发生这个转换
- TIDYING -> TERMINATED：这个前面说了，当 terminated() 方法结束后

这几个状态应该放在第一个和第二个上面,这两个是最常见的状态。

### Worker

在线程池中,Doug Lea将每个线程都封装为一个内部类——`Worker`,根据之前的介绍,任务Runnable(包装为task)将被放在任务队列中,然后使用worker来处理。而这里worker又继承了`  AbstractQueuedSynchronizer`类。

```java
private final class Worker
    extends AbstractQueuedSynchronizer
    implements Runnable
{
    private static final long serialVersionUID = 6138294804551838833L;

    // 这个是真正的线程，任务靠你啦
    final Thread thread;

    //当创建一个worker时,可以给他配置一个任务,这个任务将第一个执行
    Runnable firstTask;

    // 用于存放此线程完成的任务数，注意了，这里用了 volatile，保证可见性
    volatile long completedTasks;

    // Worker 只有这一个构造方法，传入 firstTask，也可以传 null
    Worker(Runnable firstTask) {
        setState(-1); // inhibit interrupts until runWorker
        this.firstTask = firstTask;
        // 调用 ThreadFactory 来创建一个新的线程
        this.thread = getThreadFactory().newThread(this);
    }

    // 这里调用了外部类的 runWorker 方法
    public void run() {
        runWorker(this);
    }

    ...// 其他方法没什么好看的，就是用 AQS 操作，来获取这个线程的执行权，用了独占锁
}
```

### execute

前面做了这么多的铺垫,这里到了重点,我们来看一下关于`  execute`方法。

```java
public void execute(Runnable command) {
    if (command == null)
        throw new NullPointerException();

    // 32位数据,获取线程状态和线程数量
    int c = ctl.get();

    // 如果当前线程数少于核心线程数，那么直接添加一个 worker 来执行任务，
    // 创建一个新的线程，并把当前任务 command 作为这个线程的第一个任务(firstTask)
    if (workerCountOf(c) < corePoolSize) {
        // 添加任务成功，那么就结束了。提交任务嘛，线程池已经接受了这个任务，这个方法也就可以返回了
        // 至于执行的结果，到时候会包装到 FutureTask 中。
        // 返回 false 代表线程池不允许提交任务
        if (addWorker(command, true))
            return;
        c = ctl.get();
    }
    // 到这里说明，要么当前线程数大于等于核心线程数，要么刚刚 addWorker 失败了

    // 如果线程池处于 RUNNING 状态，把这个任务添加到任务队列 workQueue 中
    if (isRunning(c) && workQueue.offer(command)) {
        //再次判断一下线程状态
        int recheck = ctl.get();
        // 如果线程池已不处于 RUNNING 状态，那么移除已经入队的这个任务，并且执行拒绝策略
        if (! isRunning(recheck) && remove(command))
            reject(command);
        // 如果线程池还是 RUNNING 的，并且线程数为 0，那么开启新的线程
        // 到这里，我们知道了，这块代码的真正意图是：担心任务提交到队列中了，但是线程都关闭了
        else if (workerCountOf(recheck) == 0)
            addWorker(null, false);
    }
    // 如果 workQueue 队列满了，那么进入到这个分支
    // 以 maximumPoolSize 为界创建新的 worker，
    // 如果失败，说明当前线程数已经达到 maximumPoolSize，执行拒绝策略
    else if (!addWorker(command, false))
        reject(command);
}
```

整个逻辑已经分析的差不多,下面我们需要看一下这里的几个重要方法。(该部分内容转自Github : Java-Tutorial)

```java
// 第一个参数是准备提交给这个线程执行的任务，之前说了，可以为 null
// 第二个参数为 true 代表使用核心线程数 corePoolSize 作为创建线程的界限，也就说创建这个线程的时候，
//         如果线程池中的线程总数已经达到 corePoolSize，那么不能响应这次创建线程的请求
//         如果是 false，代表使用最大线程数 maximumPoolSize 作为界限
private boolean addWorker(Runnable firstTask, boolean core) {
    retry:
    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);

        // 这个非常不好理解
        // 如果线程池已关闭，并满足以下条件之一，那么不创建新的 worker：
        // 1\. 线程池状态大于 SHUTDOWN，其实也就是 STOP, TIDYING, 或 TERMINATED
        // 2\. firstTask != null
        // 3\. workQueue.isEmpty()
        // 简单分析下：
        // 还是状态控制的问题，当线程池处于 SHUTDOWN 的时候，不允许提交任务，但是已有的任务继续执行
        // 当状态大于 SHUTDOWN 时，不允许提交任务，且中断正在执行的任务
        // 多说一句：如果线程池处于 SHUTDOWN，但是 firstTask 为 null，且 workQueue 非空，那么是允许创建 worker 的
        // 这是因为 SHUTDOWN 的语义：不允许提交新的任务，但是要把已经进入到 workQueue 的任务执行完，所以在满足条件的基础上，是允许创建新的 Worker 的
        if (rs >= SHUTDOWN &&
            ! (rs == SHUTDOWN &&
               firstTask == null &&
               ! workQueue.isEmpty()))
            return false;

        for (;;) {
            int wc = workerCountOf(c);
            if (wc >= CAPACITY ||
                wc >= (core ? corePoolSize : maximumPoolSize))
                return false;
            // 如果成功，那么就是所有创建线程前的条件校验都满足了，准备创建线程执行任务了
            // 这里失败的话，说明有其他线程也在尝试往线程池中创建线程
            if (compareAndIncrementWorkerCount(c))
                break retry;
            // 由于有并发，重新再读取一下 ctl
            c = ctl.get();
            // 正常如果是 CAS 失败的话，进到下一个里层的for循环就可以了
            // 可是如果是因为其他线程的操作，导致线程池的状态发生了变更，如有其他线程关闭了这个线程池
            // 那么需要回到外层的for循环
            if (runStateOf(c) != rs)
                continue retry;
            // else CAS failed due to workerCount change; retry inner loop
        }
    }

    /* 
     * 到这里，我们认为在当前这个时刻，可以开始创建线程来执行任务了，
     * 因为该校验的都校验了，至于以后会发生什么，那是以后的事，至少当前是满足条件的
     */

    // worker 是否已经启动
    boolean workerStarted = false;
    // 是否已将这个 worker 添加到 workers 这个 HashSet 中
    boolean workerAdded = false;
    Worker w = null;
    try {
        final ReentrantLock mainLock = this.mainLock;
        // 把 firstTask 传给 worker 的构造方法
        w = new Worker(firstTask);
        // 取 worker 中的线程对象，之前说了，Worker的构造方法会调用 ThreadFactory 来创建一个新的线程
        final Thread t = w.thread;
        if (t != null) {
            // 这个是整个线程池的全局锁，持有这个锁才能让下面的操作“顺理成章”，
            // 因为关闭一个线程池需要这个锁，至少我持有锁的期间，线程池不会被关闭
            mainLock.lock();
            try {

                int c = ctl.get();
                int rs = runStateOf(c);

                // 小于 SHUTTDOWN 那就是 RUNNING，这个自不必说，是最正常的情况
                // 如果等于 SHUTDOWN，前面说了，不接受新的任务，但是会继续执行等待队列中的任务
                if (rs < SHUTDOWN ||
                    (rs == SHUTDOWN && firstTask == null)) {
                    // worker 里面的 thread 可不能是已经启动的
                    if (t.isAlive())
                        throw new IllegalThreadStateException();
                    // 加到 workers 这个 HashSet 中
                    workers.add(w);
                    int s = workers.size();
                    // largestPoolSize 用于记录 workers 中的个数的最大值
                    // 因为 workers 是不断增加减少的，通过这个值可以知道线程池的大小曾经达到的最大值
                    if (s > largestPoolSize)
                        largestPoolSize = s;
                    workerAdded = true;
                }
            } finally {
                mainLock.unlock();
            }
            // 添加成功的话，启动这个线程
            if (workerAdded) {
                // 启动线程
                t.start();
                workerStarted = true;
            }
        }
    } finally {
        // 如果线程没有启动，需要做一些清理工作，如前面 workCount 加了 1，将其减掉
        if (! workerStarted)
            addWorkerFailed(w);
    }
    // 返回线程是否启动成功
    return workerStarted;
}
```

简单看下 addWorkFailed 的处理：

```
// workers 中删除掉相应的 worker
// workCount 减 1
private void addWorkerFailed(Worker w) {
    final ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
        if (w != null)
            workers.remove(w);
        decrementWorkerCount();
        // rechecks for termination, in case the existence of this worker was holding up termination
        tryTerminate();
    } finally {
        mainLock.unlock();
    }
}
```

回过头来，继续往下走。我们知道，worker 中的线程 start 后，其 run 方法会调用 runWorker 方法：

```
// Worker 类的 run() 方法
public void run() {
    runWorker(this);
}
```

继续往下看 runWorker 方法：

```
// 此方法由 worker 线程启动后调用，这里用一个 while 循环来不断地从等待队列中获取任务并执行
// 前面说了，worker 在初始化的时候，可以指定 firstTask，那么第一个任务也就可以不需要从队列中获取
final void runWorker(Worker w) {
    // 
    Thread wt = Thread.currentThread();
    // 该线程的第一个任务(如果有的话)
    Runnable task = w.firstTask;
    w.firstTask = null;
    w.unlock(); // allow interrupts
    boolean completedAbruptly = true;
    try {
        // 循环调用 getTask 获取任务
        while (task != null || (task = getTask()) != null) {
            w.lock();          
            // 如果线程池状态大于等于 STOP，那么意味着该线程也要中断
            if ((runStateAtLeast(ctl.get(), STOP) ||
                 (Thread.interrupted() &&
                  runStateAtLeast(ctl.get(), STOP))) &&
                !wt.isInterrupted())
                wt.interrupt();
            try {
                // 这是一个钩子方法，留给需要的子类实现
                beforeExecute(wt, task);
                Throwable thrown = null;
                try {
                    // 到这里终于可以执行任务了
                    task.run();
                } catch (RuntimeException x) {
                    thrown = x; throw x;
                } catch (Error x) {
                    thrown = x; throw x;
                } catch (Throwable x) {
                    // 这里不允许抛出 Throwable，所以转换为 Error
                    thrown = x; throw new Error(x);
                } finally {
                    // 也是一个钩子方法，将 task 和异常作为参数，留给需要的子类实现
                    afterExecute(task, thrown);
                }
            } finally {
                // 置空 task，准备 getTask 获取下一个任务
                task = null;
                // 累加完成的任务数
                w.completedTasks++;
                // 释放掉 worker 的独占锁
                w.unlock();
            }
        }
        completedAbruptly = false;
    } finally {
        // 如果到这里，需要执行线程关闭：
        // 1\. 说明 getTask 返回 null，也就是说，队列中已经没有任务需要执行了，执行关闭
        // 2\. 任务执行过程中发生了异常
        // 第一种情况，已经在代码处理了将 workCount 减 1，这个在 getTask 方法分析中会说
        // 第二种情况，workCount 没有进行处理，所以需要在 processWorkerExit 中处理
        // 限于篇幅，我不准备分析这个方法了，感兴趣的读者请自行分析源码
        processWorkerExit(w, completedAbruptly);
    }
}
```

我们看看 getTask() 是怎么获取任务的，这个方法写得真的很好，每一行都很简单，组合起来却所有的情况都想好了：

```
// 此方法有三种可能：
// 1\. 阻塞直到获取到任务返回。我们知道，默认 corePoolSize 之内的线程是不会被回收的，
//      它们会一直等待任务
// 2\. 超时退出。keepAliveTime 起作用的时候，也就是如果这么多时间内都没有任务，那么应该执行关闭
// 3\. 如果发生了以下条件，此方法必须返回 null:
//    - 池中有大于 maximumPoolSize 个 workers 存在(通过调用 setMaximumPoolSize 进行设置)
//    - 线程池处于 SHUTDOWN，而且 workQueue 是空的，前面说了，这种不再接受新的任务
//    - 线程池处于 STOP，不仅不接受新的线程，连 workQueue 中的线程也不再执行
private Runnable getTask() {
    boolean timedOut = false; // Did the last poll() time out?

    retry:
    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);
        // 两种可能
        // 1\. rs == SHUTDOWN && workQueue.isEmpty()
        // 2\. rs >= STOP
        if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
            // CAS 操作，减少工作线程数
            decrementWorkerCount();
            return null;
        }

        boolean timed;      // Are workers subject to culling?
        for (;;) {
            int wc = workerCountOf(c);
            // 允许核心线程数内的线程回收，或当前线程数超过了核心线程数，那么有可能发生超时关闭
            timed = allowCoreThreadTimeOut || wc > corePoolSize;

            // 这里 break，是为了不往下执行后一个 if (compareAndDecrementWorkerCount(c))
            // 两个 if 一起看：如果当前线程数 wc > maximumPoolSize，或者超时，都返回 null
            // 那这里的问题来了，wc > maximumPoolSize 的情况，为什么要返回 null？
            //    换句话说，返回 null 意味着关闭线程。
            // 那是因为有可能开发者调用了 setMaximumPoolSize() 将线程池的 maximumPoolSize 调小了，那么多余的 Worker 就需要被关闭
            if (wc <= maximumPoolSize && ! (timedOut && timed))
                break;
            if (compareAndDecrementWorkerCount(c))
                return null;
            c = ctl.get();  // Re-read ctl
            // compareAndDecrementWorkerCount(c) 失败，线程池中的线程数发生了改变
            if (runStateOf(c) != rs)
                continue retry;
            // else CAS failed due to workerCount change; retry inner loop
        }
        // wc <= maximumPoolSize 同时没有超时
        try {
            // 到 workQueue 中获取任务
            Runnable r = timed ?
                workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                workQueue.take();
            if (r != null)
                return r;
            timedOut = true;
        } catch (InterruptedException retry) {
            // 如果此 worker 发生了中断，采取的方案是重试
            // 解释下为什么会发生中断，这个读者要去看 setMaximumPoolSize 方法。

            // 如果开发者将 maximumPoolSize 调小了，导致其小于当前的 workers 数量，
            // 那么意味着超出的部分线程要被关闭。重新进入 for 循环，自然会有部分线程会返回 null
            timedOut = false;
        }
    }
}
```

到这里，基本上也说完了整个流程，读者这个时候应该回到 execute(Runnable command) 方法，看看各个分支，我把代码贴过来一下：

```
public void execute(Runnable command) {
    if (command == null)
        throw new NullPointerException();

    // 前面说的那个表示 “线程池状态” 和 “线程数” 的整数
    int c = ctl.get();

    // 如果当前线程数少于核心线程数，那么直接添加一个 worker 来执行任务，
    // 创建一个新的线程，并把当前任务 command 作为这个线程的第一个任务(firstTask)
    if (workerCountOf(c) < corePoolSize) {
        // 添加任务成功，那么就结束了。提交任务嘛，线程池已经接受了这个任务，这个方法也就可以返回了
        // 至于执行的结果，到时候会包装到 FutureTask 中。
        // 返回 false 代表线程池不允许提交任务
        if (addWorker(command, true))
            return;
        c = ctl.get();
    }
    // 到这里说明，要么当前线程数大于等于核心线程数，要么刚刚 addWorker 失败了

    // 如果线程池处于 RUNNING 状态，把这个任务添加到任务队列 workQueue 中
    if (isRunning(c) && workQueue.offer(command)) {
        /* 这里面说的是，如果任务进入了 workQueue，我们是否需要开启新的线程
         * 因为线程数在 [0, corePoolSize) 是无条件开启新的线程
         * 如果线程数已经大于等于 corePoolSize，那么将任务添加到队列中，然后进到这里
         */
        int recheck = ctl.get();
        // 如果线程池已不处于 RUNNING 状态，那么移除已经入队的这个任务，并且执行拒绝策略
        if (! isRunning(recheck) && remove(command))
            reject(command);
        // 如果线程池还是 RUNNING 的，并且线程数为 0，那么开启新的线程
        // 到这里，我们知道了，这块代码的真正意图是：担心任务提交到队列中了，但是线程都关闭了
        else if (workerCountOf(recheck) == 0)
            addWorker(null, false);
    }
    // 如果 workQueue 队列满了，那么进入到这个分支
    // 以 maximumPoolSize 为界创建新的 worker，
    // 如果失败，说明当前线程数已经达到 maximumPoolSize，执行拒绝策略
    else if (!addWorker(command, false))
        reject(command);
}
```

上面各个分支中，有两种情况会调用 reject(command) 来处理任务，因为按照正常的流程，线程池此时不能接受这个任务，所以需要执行我们的拒绝策略。接下来，我们说一说 ThreadPoolExecutor 中的拒绝策略。

```
final void reject(Runnable command) {
    // 执行拒绝策略
    handler.rejectedExecution(command, this);
}
```

此处的 handler 我们需要在构造线程池的时候就传入这个参数，它是 RejectedExecutionHandler 的实例。

RejectedExecutionHandler 在 ThreadPoolExecutor 中有四个已经定义好的实现类可供我们直接使用，当然，我们也可以实现自己的策略，不过一般也没有必要。

## 总结

我一向不喜欢写总结，因为我把所有需要表达的都写在正文中了，写小篇幅的总结并不能真正将话说清楚，本文的总结部分为准备面试的读者而写，希望能帮到面试者或者没有足够的时间看完全文的读者。

1. java 线程池有哪些关键属性？

   > corePoolSize，maximumPoolSize，workQueue，keepAliveTime，rejectedExecutionHandler
   >
   > corePoolSize 到 maximumPoolSize 之间的线程会被回收，当然 corePoolSize 的线程也可以通过设置而得到回收（allowCoreThreadTimeOut(true)）。
   >
   > workQueue 用于存放任务，添加任务的时候，如果当前线程数超过了 corePoolSize，那么往该队列中插入任务，线程池中的线程会负责到队列中拉取任务。
   >
   > keepAliveTime 用于设置空闲时间，如果线程数超出了 corePoolSize，并且有些线程的空闲时间超过了这个值，会执行关闭这些线程的操作
   >
   > rejectedExecutionHandler 用于处理当线程池不能执行此任务时的情况，默认有**抛出 RejectedExecutionException 异常**、**忽略任务**、**使用提交任务的线程来执行此任务**和**将队列中等待最久的任务删除，然后提交此任务**这四种策略，默认为抛出异常。

2. 说说线程池中的线程创建时机？

   > 1. 如果当前线程数少于 corePoolSize，那么提交任务的时候创建一个新的线程，并由这个线程执行这个任务；
   > 2. 如果当前线程数已经达到 corePoolSize，那么将提交的任务添加到队列中，等待线程池中的线程去队列中取任务；
   > 3. 如果队列已满，那么创建新的线程来执行任务，需要保证池中的线程数不会超过 maximumPoolSize，如果此时线程数超过了 maximumPoolSize，那么执行拒绝策略。

   - 注意：如果将队列设置为无界队列，那么线程数达到 corePoolSize 后，其实线程数就不会再增长了。因为后面的任务直接往队列塞就行了，此时 maximumPoolSize 参数就没有什么意义。

3. Executors.newFixedThreadPool(…) 和 Executors.newCachedThreadPool() 构造出来的线程池有什么差别？

   > 细说太长，往上滑一点点，在 Executors 的小节进行了详尽的描述。

4. 任务执行过程中发生异常怎么处理？

   > 如果某个任务执行出现异常，那么执行任务的线程会被关闭，而不是继续接收其他任务。然后会启动一个新的线程来代替它。

5. 什么时候会执行拒绝策略？

   > 1. workers 的数量达到了 corePoolSize（任务此时需要进入任务队列），任务入队成功，与此同时线程池被关闭了，而且关闭线程池并没有将这个任务出队，那么执行拒绝策略。这里说的是非常边界的问题，入队和关闭线程池并发执行，读者仔细看看 execute 方法是怎么进到第一个 reject(command) 里面的。
   > 2. workers 的数量大于等于 corePoolSize，将任务加入到任务队列，可是队列满了，任务入队失败，那么准备开启新的线程，可是线程数已经达到 maximumPoolSize，那么执行拒绝策略。

## 参考文章

- [**深度解读 java 线程池设计思想及源码实现**](https://github.com/h2pl/Java-Tutorial/blob/master/docs/java/currency/Java%E5%B9%B6%E5%8F%91%E6%8C%87%E5%8D%9712%EF%BC%9A%E6%B7%B1%E5%BA%A6%E8%A7%A3%E8%AF%BB%20java%20%E7%BA%BF%E7%A8%8B%E6%B1%A0%E8%AE%BE%E8%AE%A1%E6%80%9D%E6%83%B3%E5%8F%8A%E6%BA%90%E7%A0%81%E5%AE%9E%E7%8E%B0.md)
- [**解读 Java 阻塞队列 BlockingQueue**]([https://github.com/h2pl/Java-Tutorial/blob/master/docs/java/currency/Java%E5%B9%B6%E5%8F%91%E6%8C%87%E5%8D%9711%EF%BC%9A%E8%A7%A3%E8%AF%BB%20Java%20%E9%98%BB%E5%A1%9E%E9%98%9F%E5%88%97%20BlockingQueue.md](https://github.com/h2pl/Java-Tutorial/blob/master/docs/java/currency/Java并发指南11：解读 Java 阻塞队列 BlockingQueue.md))
- [**线程池拒绝策略**](https://github.com/h2pl/Java-Tutorial/blob/master/docs/java/currency/Java%E5%B9%B6%E5%8F%91%E6%8C%87%E5%8D%9711%EF%BC%9A%E8%A7%A3%E8%AF%BB%20Java%20%E9%98%BB%E5%A1%9E%E9%98%9F%E5%88%97%20BlockingQueue.md)