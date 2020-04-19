

### 简介

在分析J.U.C包下的各项源码时,多多少少都会遇到一些`AbstractQueuedSynchronizer`的内容,因为其是实现`ReentrantLock`,`CountDownLatch等`功能的基础。

而之前自己在网上Google关于AQS的内容发现,好多博客大同小异,并没有真正分析清除AQS的原理所在,所以希望能够从源码的角度,按照自己的思维整理一篇关于AQS相关的博客,对于后续需要总结的`Lock`相关的内容有所帮助。

注意 : 

1. 本文使用JDK1.8作为基础,可以结合源码进行理解
2. 本文基于`ReentrantLock`中的公平锁来阐述,非公平锁和公平锁的区别很小,易于理解
3. 本文将以配图形式尽量完整阐述整个流程思路,但是如果有不易表达的地方,还是应该多看源码,J.U.C下的源码很优雅
4. 本文结合个人理解,如有错误之处,请评论指正或联系作者,谢谢

-----------------------------------------

### AQS结构

学习AQS需要自顶向下然后再自底向上,如果直接从最底层的Node开始学习,过程会异常困难。所以可以优先理解整个AQS的设计思路,其内部有哪些对象,整体工作模式后再进行详细的拆解。

一下这几个是需要重点理解的一些变量,可以看到其中很多变量都被`  `volatile 修饰,主要是解决缓存一致性的问题,之后在源码中会经常见到,不再赘述。

```java
//AQS的头节点,注意头节点永远不会参与线程竞争
private transient volatile Node head;
//AQS尾节点
private transient volatile Node tail;
//锁状态,Lock中可重入的关键
private volatile int state;
//自旋时间1S
static final long spinForTimeoutThreshold = 1000L;
//大名鼎鼎的Unsafe类,JUC中阻塞线程使用的是该类park方法而非notify
private static final Unsafe unsafe = Unsafe.getUnsafe();
```

![](https://s1.ax1x.com/2020/04/12/GqI9Pg.png)

图中可以看到,头节点是一个独立的节点,该节点不实际参与到排队中,至于为什么这样设计,在之后会阐述。

AQS基本结构就是这样,那么节点`  Node`如何设计的,我们就需要根据源码再来看一下

### Node

`  Node`是AQS同步队列的一个内部类,其中对于节点做了大量的处理来适配各种节点的操作以供AQS使用,如果能够把`  Node`类的源码弄清除,实际上大部分的内容就很容易理解,首先来看`  Node`中的几个变量,这也是理解Node类的重点。

```java
//标志当前节点在共享模式下
static final Node SHARED = new Node();
//标志当前节点在独占模式下
static final Node EXCLUSIVE = null;

//几个非常重要的节点状态变量,用于标志节点状态以及condition的使用,后续会进行详细阐述
static final int CANCELLED =  1;
static final int SIGNAL    = -1;
static final int CONDITION = -2;
static final int PROPAGATE = -3;
//节点等待状态,和上面的几个状态值有关
volatile int waitStatus;
//前驱节点
volatile Node prev;
//后驱节点
volatile Node next;
//队列中的阻塞线程
volatile Thread thread;
//由于条件队列仅在处于独占模式时才被访问，因此我们只需要一个简单的链接队列即可在节点等待条件时保存节点
Node nextWaiter;
```

本篇文章仅仅针对AQS来说,对于条件队列会在之后的内容中做介绍,所以现在先不去阐述。

首先来看前两个变量,`  SHEAD`和`  EXCLUSIVE`。在Node中通过这两个变量来表示这个模式是独占模式还是共享模式

- 独占模式 : 锁只能够被一个线程获取，`  reentrantlock`就是独占锁
- 共享模式 : 共享锁能够被多个线程获取

这篇文章结合`  reentrantlock`来讲解,所以优先说独占锁的相关内容。

结合独占锁的知识我们知道,当多个线程对锁竞争时,只有一个线程能够获取到锁,而其他线程如果在自旋后依旧没有获取到,则会被放入AQS队列中阻塞,那么这个入队的过程时什么样的,来看下源码。

`  addWaiter`方法中非常清晰的展示了一个Node如何入队的过程。

```java
/**
     * 为当前线程和给定模式创建节点
     *
     * @param mode Node.EXCLUSIVE作为独占锁, Node.SHARED为共享锁
     * @return the new node
     */
private Node addWaiter(Node mode) {
    //将当前线程和模式封装为一个Node
    Node node = new Node(Thread.currentThread(), mode);
    //找到AQS队列中的Tail
    Node pred = tail;
    //如果尾部不为空,说明队列已经完成了初始化,则将前驱节点指向尾部,自己则称为队列尾部
    if (pred != null) {
        node.prev = pred;
        //注意 : 在AQS中,大部分操作都需要通过CAS来进行,因为入队出队等过程都可能发生线程竞争,需要保证安全
        if (compareAndSetTail(pred, node)) {
            pred.next = node;
            return node;
        }
    }
    enq(node);
    return node;
}
```

如果之前线程从来没发生过竞争,也就意味着此时`  node`是第一个进入到AQS队列中的元素,那么就需要对队列进行初始化处理,此时调用`  enq`方法。

```java
	/**
     * Inserts node into queue, initializing if necessary. See picture above.
     * @param node the node to insert
     * @return node's predecessor
     */
    private Node enq(final Node node) {
        //整个操作是一个死循环,只有成功初始化队列才能够退出
        for (;;) {
            Node t = tail;
            if (t == null) {
                //CAS设置头部
                if (compareAndSetHead(new Node()))
                    tail = head;
            } else {
                //CAS设置尾部,改变新入队的Node的双指针指向
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }
```

我们可以看到,这里面使用了大量的CAS操作,而这些CAS操作实际上都来自一个类`  Unsafe`。

```java
private final boolean compareAndSetHead(Node update) {
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }
//在Unsafe中的实现
public final native boolean compareAndSwapObject(Object var1, long var2, Object var4, Object var5);
```

可以看到这是一个native方法,但本质上也是通过偏移量进行校验。由于数据在内存中是通过偏移量的方式存储,而这些偏移量是在整个类加载的时候就进行了赋值。

```java
private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long stateOffset;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long waitStatusOffset;
    private static final long nextOffset;

    static {
        try {
            stateOffset = unsafe.objectFieldOffset
                (AbstractQueuedSynchronizer.class.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset
                (AbstractQueuedSynchronizer.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset
                (AbstractQueuedSynchronizer.class.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset
                (Node.class.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset
                (Node.class.getDeclaredField("next"));

        } catch (Exception ex) { throw new Error(ex); }
    }
```

这段代码就是对于CAS操作中所用到的偏移量的初始化赋值,在之后的操作都调用了这些方法,还可以看到这里罕见的抛出了一个`  Error`,这是由于Unsafe类能够操作堆外内存,如果偏移量设置错误,被Unsafe操作的内存可能并不是期望的内存,这种结果会造成巨大的隐患,所以这部分需要抛出Error来中止整个程序而非进行捕获处理。

### waitStatus

之前在源码中看到了这个变量,该变量主要表示当前节点的状态,当到达队首时,需要判断一下再进行执行,在一个节点初始化的情况下,默认的节点状态值为0.

- **CANCELLED**(1)：表示当前结点已取消调度。当timeout或被中断（响应中断的情况下），会触发变更为此状态，进入该状态后的结点将不会再变化。
- **SIGNAL**(-1)：表示后继结点在等待当前结点唤醒。后继结点入队时，会将前继结点的状态更新为SIGNAL。
- **CONDITION**(-2)：表示结点等待在Condition上，当其他线程调用了Condition的signal()方法后，CONDITION状态的结点将**从等待队列转移到同步队列中**，等待获取同步锁。
- **PROPAGATE**(-3)：共享模式下，前继结点不仅会唤醒其后继结点，同时也可能会唤醒后继的后继结点。
- **0**：新结点入队时的默认状态。

AQS需要根据状态来决定唤醒哪些节点,这也是AQS支持Condition和ShareLock的一个关键因素。

### 唤醒和休眠过程

在AQS中的Node所拥有的线程应该是阻塞的线程,而这一过程是如何完成的,或者说通过waitStatus如何影响线程的阻塞和唤醒过程,这是这部分内容主要考虑的问题。

线程唤醒是一件比较简单的事情,但是在这里需要考虑的不仅仅是唤醒线程,还要考虑到该节点之后的节点状态量`  waitStatus`的变化情况,只有这样才能够保证整个节点的有效性,可以这样归纳。

> **负值表示结点处于有效等待状态，而正值表示结点已被取消。所以源码中很多地方用>0、<0来判断结点的状态是否正常**。

这里需要说明一个我个人的理解 : 看了很多篇文章都没有讲明白为什么waitStatus需要用两种类型区分(大于0,小于0),个人认为可以用生活中的一个例子来解释。

什么是排队 ? 假如我们在公园买票需要排队 , 那么这个队列看作AQS，那么售票员就可以看作队列的`HEAD`,而正在买票的人可以看作是一个`  waitStatus`大于0的Node,其余人都是小于0,实际上真正等待的是第一个人后面的人,而第一个人并不是等待状态,而是正处于办理业务。

对应到AQS中,第一个Node并不是wait节点,因为它正在被移除队列,只不过目前资源没有被释放,而后面的这些节点才是真正的等待节点。

![](https://s1.ax1x.com/2020/04/12/GLJ5m6.png)

首先来看一下唤醒线程的方法`  unparkSuccessor`

```java
private void unparkSuccessor(Node node) {
        //获取到当前Node的等待状态
        int ws = node.waitStatus;
    	//如果等待状态有效,则取消其等待状态
        if (ws < 0)
            compareAndSetWaitStatus(node, ws, 0);

        //检查当前节点是否为空,如果为空则唤醒当前节点最近的后继节点
        Node s = node.next;
        if (s == null || s.waitStatus > 0) {
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0)
                    s = t;
        }
    	//不为空,直接唤醒
        if (s != null)
            LockSupport.unpark(s.thread);
    }
```

结合我写的注释,这个过程就容易理解一些,那么我们再来看一下这个歌唤醒线程的方法`  unpark()`。实际上能够通过多种模式来实现,但是需要权衡,而unpark是最合适的机制。

回忆一下,我们可以用哪些方法让线程阻塞并唤醒 : 

- wait/notify/notifyAll
- sleep
- park/unpark

而第一种,我们并没有办法指定唤醒某个线程,这个在逻辑上就没有办法实现AQS这种唤醒首节点的机制,而第二种虽然勉强可以,但是sleep在使用时依旧会占用资源,并且需要时限,我们无法预期某个程序会运行多久,也就没有办法精准唤醒某个线程。

而`  park`和`  unpark`方法就能够很好的帮我们解决这个问题,真正的`  用户态 ——> 内核态`的阻塞,能够精准唤醒这两个特点能让AQS正常运行。同时LockSuports实际上是封装了一些`  unsafe`类的操作,而直接调用native方法是更加节省资源和高效的。

而阻塞的过程就非常容易理解,同样使用LockSuports来解决,代码也十分简洁。

```java
private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        return Thread.interrupted();
    }
```

直接调用了`park`方法然后返回了线程中断结果。

### acquire & tryAcquire 

AQS中必然会提到的大名鼎鼎的方法`  acquire`,该方法是获取一把锁时必须要经过的流程,因此,逐行分析这个方法是非常重要并且十分有意义的行为。

该方法内还调用了其他的方法,我们逐层来看 : 

```java
public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
```

首先来说下这三个函数 : 

- `  tryAcquire` : 尝试获取锁,如果失败则返回false
- `  acquireQueued` : 获取队列并将一个Node入列
- `selfInterrupt` : 打断当前线程

逻辑上来说,线程被阻塞的情景条件有两个,未获取到锁和成功入列。那么先来看第一个条件,也就是`  tryAcquire`方法 ,由于在`  AbstractQueuedSynchronizer`中,没有实现该方法,而是由其子类进行实现,所以我们这里展示的是公平锁的实现。

```java
protected final boolean tryAcquire(int acquires) {
    //获取当前线程
    final Thread current = Thread.currentThread();
    //获取当前锁的状态值
    int c = getState();
    //判断目前锁状态,如果没有线程持有锁
    if (c == 0) {
        //通过CAS赋值并检查队列
        if (!hasQueuedPredecessors() &&
            compareAndSetState(0, acquires)) {
            //将该线程设置未锁的独占线程
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    //如果锁已经被当前线程持有,则重入
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        if (nextc < 0)
            throw new Error("Maximum lock count exceeded");
        setState(nextc);
        return true;
    }
    //获取锁失败
    return false;
}
```

这段代码写的真的非常清晰,整个思路和流畅性完全没有问题,哪怕没有注释,相信大部分人读一读都能够理解这个获取锁的过程。

然后再来看`  Acquire`的下一个方法`  acquireQueued`

```java
final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {
            //获取当前节点的前去节点
            final Node p = node.predecessor();
            //如果p是头节点,说明这个节点是队列中的第一个节点,会再次尝试一下获取锁(自旋操作)
            if (p == head && tryAcquire(arg)) {
                //如果成功,则把头节点指向空,该节点获取到了锁
                setHead(node);
                p.next = null; // help GC
                failed = false;
                return interrupted;
            }
            //如果失败了,就将该节点添加进入AQS中
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                //将线程中断状态置为true
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
```

很明显,在这个过程中实际上希望做一个自旋的操作来保证性能,减少用户态和内核态来回转换导致的性能损耗,而失败后则会将该节点放入AQS中。

而最后一个方法则非常简单,直接中断当前线程。

```java
static void selfInterrupt() {
    Thread.currentThread().interrupt();
}
```

以上这些内容是有关于获取锁的方法,下面我们再看看其他的内容。

---------------------

### release & tryRelease

刚才说到了获取锁,那么来说一下释放锁,释放锁也是一个很有意思的操作,不过公平锁和非公平锁并没有区分,所以该方法是在`  SYNC`这个内部类下。

```java
protected final boolean tryRelease(int releases) {
    //减少锁的层数,因为是重入锁
    int c = getState() - releases;
    //如果不是当前线程释放,则报异常,这种情况很少
    if (Thread.currentThread() != getExclusiveOwnerThread())
        throw new IllegalMonitorStateException();
    boolean free = false;
    //如果state状态为0,说明没有线程持有锁
    if (c == 0) {
        free = true;
        //将所拥有线程释放
        setExclusiveOwnerThread(null);
    }
    setState(c);
    return free;
}
```

再来看下`  release`方法

```java
public final boolean release(int arg) {
    //如果线程已经将锁释放
    if (tryRelease(arg)) {
        //获取头部
        Node h = head;
        //如果头部存在并且头部不是新建状态
        if (h != null && h.waitStatus != 0)
            //释放头部后驱最近的节点
            unparkSuccessor(h);
        return true;
    }
    return false;
}
```

由此可见,tryRelease主要用于某个线程释放锁,而release则是当锁被释放后,唤醒队列中第一个节点。