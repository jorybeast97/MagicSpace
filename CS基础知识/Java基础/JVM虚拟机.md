## JVM虚拟机结构

![](https://s2.ax1x.com/2020/02/20/3ZN2qJ.png)

### 虚拟机栈(栈)

虚拟机栈本质上就是一个数据结构栈，具有FILO的特点(first in , last out)，在执行方法的时候会压入栈中，而在本地方法栈中有如下几块内容 : 

- 局部变量表 : 方法中的各种变量会在这部分分配一块内存。
- 操作数 : 对于逻辑的操作判断等
- 动态链接 : 将符号引用转为直接引用(内存地址)
- 方法出口 : 跳转到调用该方法的方法 , 最后的方法出口一定是main函数

### 方法区(永久代 -> JDK1.8后元空间)

方法区储存Class的静态量，类信息，可以看作是Class类型的堆(每个类实际上都是Class类的对象)，如果变量中包含有对象，则储存对象在heap中的内存地址。

### 本地方法栈

早先使用的C语言编写的一些方法，和虚拟机栈没有本质区别，native存储位置

### 字节码执行引擎

对编译完成后的Class文件进行执行的部分

### 类装载系统

能够把加载完成的.class文件装在进入JVM

### Heap堆(重点)

![](https://s2.ax1x.com/2020/02/20/3ZdSKS.png)

#### 可达性算法

由于引用计数算法无法解决对象相互引用的问题，所以JVM使用的是可达性算法,根据GC Roots向下遍历啊，在链上存引用的对象都是不可回收的对象，其他对象都是可回收的,当对象满后，进行minor GC(年轻代GC)

**可以作为GC Root的变量**

- 通过System Class Loader或者Boot Class Loader加载的class对象，通过自定义类加载器加载的class不一定是GC Root
- 处于激活状态的线程
- 栈中的对象
- JNI栈中的对象(Java Native Interface 本地方法接口)
- JNI中的全局对象
- 正在被用于同步的各种锁对象
- JVM自身持有的对象，比如系统类加载器等。

#### 分代年龄

在年轻代中，会在From和To区中时刻保持一个区是空的，每当进行minorGC时，会把当前存活的对象放入空的区，然后分代年龄加一，当分代年龄到达15时，会放入老年代。

from和to主要就是为了来回交换数据所设置的。

老年代一般是具有长时间存活特性的对象，例如静态对象，线程池中，Bean容器中对象等。

#### 老年代GC

当Old对象满后进行Full GC，Full GC将对年轻代，方法区，以及自身都会进行收集，速度相对来说会慢很多。

#### JAVA对象头

![](https://s2.ax1x.com/2020/02/20/3Z0YHe.png)

-------------

## JMM内存模型

### JAVA线程内存模型

实际上Java线程内存模型和CPU缓存模型非常相似,当和内存中共享变量发生关系时，会在工作内存中创建一个副本，这就带来了**缓存一致性问题**。(MESI缓存一致性协议，CPU通过总线嗅探机制完成对内存的强制刷新)

![](https://s2.ax1x.com/2020/02/20/3ZRgy9.png)

### JMM原子操作

![](https://s2.ax1x.com/2020/02/20/3ePUXQ.png)

- lock(锁定)：作用于主内存，它把一个变量标记为一条线程独占状态；
- read(读取)：作用于主内存，它把变量值从主内存传送到线程的工作内存中，以便随后的load动作使用；
- load(载入)：作用于工作内存，它把read操作的值放入工作内存中的变量副本中；
- use(使用)：作用于工作内存，它把工作内存中的值传递给执行引擎，每当虚拟机遇到一个需要使用这个变量的指令时候，将会执行这个动作；
- assign(赋值)：作用于工作内存，它把从执行引擎获取的值赋值给工作内存中的变量，每当虚拟机遇到一个给变量赋值的指令时候，执行该操作；
- store(存储)：作用于工作内存，它把工作内存中的一个变量传送给主内存中，以备随后的write操作使用；
- write(写入)：作用于主内存，它把store传送值放到主内存中的变量中。

### Volatile底层原理

> 当使用volatile修饰变量时，就会在总线开启**MESI缓存一致性协议**，同时CPU将开启**总线嗅探机制**。即对应在汇编层面添加**Lock前缀执行**

#### Lock指令

lock指令作用为，锁定这块区域的缓存(缓存行锁定)并写回到主内存中

1. 会将当前的缓存数据立刻写回到主内存中
2. 这个写操作将立刻通过总线嗅探机制使得其他缓存中的该变量立即无效化，需要重新读取主内存最新变量。

## 垃圾回收算法

### 概念

#### 四种引用

Java中有四种引用，强，软，弱，幽灵

- 强引用

```java
Object obj = new Object();
```

- 软引用 : 只有在内存不够的情况下才会回收

```
SoftReference<Object> sf = new SoftReference<Object>(obj);
```

- 弱引用 : 弱引用一定会回收，生命只能持续到下次垃圾回收之前

```
WeakReference<Object> wf = new WeakReference<Object>(obj);
```

- 幽灵引用 : 为一个对象设置虚引用的唯一目的是能在这个对象被回收时收到一个系统通知。

```java
PhantomReference<Object> pf = new PhantomReference<Object>(obj, null);
```

#### finalize方法

隶属于Object类中的方法，当对象回收的时候，并不会直接消灭，而是放在队列中，类似于缓刑操作，然后会再次检查对象的引用信息，在回收对象时，如果对象类重写了finalize方法，并在回收的时候重新引用了一个GCRoots，则就能够避免死刑，从队列中弹出，否则这些对象就真的被回收了。

### 标记——清除

![](https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/005b481b-502b-4e3f-985d-d043c2b330aa.png)

标记清楚算法时最简单的GC算法，通过可达性分析得到GC不可达对象，然后将他们一个一个回收。但是这种算法效率低并且容易产生大量的不连续内存地址，当再次分配大型对象时，需要重新触发GC。

在标记阶段，需要检查每一个对象，清除阶段，将回收后的空闲地址用链表连接起来，当再次需要分配对象时，遍历链表寻找空闲地址即可。

### 标记——整理算法

![](https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/ccd773a5-ad38-4022-895c-7ac318f31437.png)

将所有存活对象向一端移动，然后清除回收对象。

- 不会产生内存碎片
- 大量的对象移动操作效率低

### 复制算法

![](https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/b2b77b9e-958c-4016-8ae5-9c6edd83871e.png)

将内存分为两块区域，Eden和Survivor，每次GC的时候，将Eden中存活的对象复制到Survivor，然后清空整个Eden区，GC完成后再将Survivor复制回Eden。效率高，但是会使用额外内存作为交换缓存区域。

HotSpot 虚拟机的 Eden 和 Survivor 大小比例默认为 8:1，保证了内存的利用率达到 90%。如果每次回收有多于 10% 的对象存活，那么一块 Survivor 就不够用了，此时需要依赖于老年代进行空间分配担保，也就是借用老年代的空间存储放不下的对象。

### 分代收集算法

分代收集算法实际上是针对不同年龄的对象进行不同的策略回收，并不是一种特殊的算法。一般来说会分为新生代和老生代两块完整的区域(G1收集器不同)

![](https://s2.ax1x.com/2020/02/20/3eaCGt.png)

#### Minor GC

由于年轻代的对象大部分都是存活时间非常短，可以快速回收的垃圾，所以特别适合复制算法，只需要用很小的一部分空间，而from和to两块空间共同构成了survivor区域，一般survivor和eden内存大小比率为1 : 8。当内存回收时，会将所有存在的内存放在from中，然后再下次回收时，再将存活的移动到to中，这样就能够保证这部分对象不需要太过频繁的移动。

- 每次存活都会是分代年龄增加1，当到达15的时候，将被送到老年代
- 当一批对象存活，并且这批对象的大小大于from或者to的一半，则不检查分代年龄，直接全部送入老年代

#### Full GC

由于大部分老年代的对象都是存活很久，例如连接池对象，Bean中注册的对象，静态对象等等，这部分对象一般来说很难死亡，回收效率也不高，所以使用标记清除或者标记整理都可以实现，当老年代对象到达上限后，将进行依次Full GC，这种GC是非常消耗性能，它将回收老年代，年轻代以及永久代(方法区)中所有的内容，会导致STW时间边长，一般需要注意系统不要再短时间内发生过多的Full GC，如果出现这种情况，要通过适当调整年轻代的大小保证不要有过多对象进入老年代。

-------------------

## 垃圾收集器

![](https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/c625baa0-dde6-449e-93df-c3a67f2f430f.jpg)

- 单线程与多线程：单线程指的是垃圾收集器只使用一个线程，而多线程使用多个线程；
- 串行与并行：串行指的是垃圾收集器与用户程序交替执行，这意味着在执行垃圾收集的时候需要停顿用户程序；并行指的是垃圾收集器和用户程序同时执行。除了 CMS 和 G1 之外，其它垃圾收集器都是以串行的方式执行。

### Serial

单线程最经典的垃圾收集前，每次收集都需要STW

![](https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/22fda4ae-4dd5-489d-ab10-9ebfdad22ae0.jpg)

由于其单线程并且设计简单，适合很小的应用程序，当堆内存比较小的时候，STW的时间非常短

### ParNew

![img](https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/81538cd5-1bcf-4e31-86e5-e198df1e013b.jpg)

它是 Serial 收集器的多线程版本。

它是 Server 场景下默认的新生代收集器，除了性能原因外，主要是因为除了 Serial 收集器，只有它能与 CMS 收集器配合使用。

### Parallel Scavenge

设计旨在提高服务器的吞吐量。

- 吞吐量 : 单位时间CPU执行任务的比率

吞吐量优先的垃圾回收器要求尽量再短的时间内收集更多的垃圾，关注点在于每次回收更多的垃圾而不是让每次停顿时间更短。所以Parallel Scavenge适合交互较少，后台运行较多的系统。

可以通过一个开关参数打开 GC 自适应的调节策略（GC Ergonomics），就不需要手工指定新生代的大小（-Xmn）、Eden 和 Survivor 区的比例、晋升老年代对象年龄等细节参数了。虚拟机会根据当前系统的运行情况收集性能监控信息，动态调整这些参数以提供最合适的停顿时间或者最大的吞吐量。

### Serial Old

![img](https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/08f32fd3-f736-4a67-81ca-295b2a7972f2.jpg)

是 Serial 收集器的老年代版本，也是给 Client 场景下的虚拟机使用。如果用在 Server 场景下，它有两大用途：

- 在 JDK 1.5 以及之前版本（Parallel Old 诞生以前）中与 Parallel Scavenge 收集器搭配使用。
- 作为 CMS 收集器的后备预案，在并发收集发生 Concurrent Mode Failure 时使用。

### Parallel Scavenge

![img](https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/278fe431-af88-4a95-a895-9c3b80117de3.jpg)

是 Parallel Scavenge 收集器的老年代版本。

在注重吞吐量以及 CPU 资源敏感的场合，都可以优先考虑 Parallel Scavenge 加 Parallel Old 收集器。

### CMS收集器

![](https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/62e77997-6957-4b68-8d12-bfd609bb2c68.jpg)

CMS收集器是划时代的一款收集器，也是所谓第一款并发收集器(伪并发)

CMS收集主要分为一下四个状态 : 

- 初始标记 : 根据GC Roots标记GC Root直接关联的对象，此阶段需要STW
- 并发标记 : 进行GC Root Tracing，标记和GC Root关联的对象，这个阶段是垃圾收集中最耗时的阶段，这个阶段将以并发的方式执行，所以不需要停止应用线程。
- 重新标记 : 修正并发标记时产生变化的一些对象及相关引用，这个阶段需要STW
- 并发清除 : 清除对象，并发进行

虽然通过初始标记和并发标记及重新标记后，大部分的对象已经能够正确的回收，但是再实际使用中，还是会出现一些对象没有被回收的情况，这类情况主要发生在并发清除阶段，称之为**浮动垃圾**，除此之外还有其他缺点。

- 吞吐量低：低停顿时间是以牺牲吞吐量为代价的，导致 CPU 利用率不够高。
- 无法处理浮动垃圾，可能出现 Concurrent Mode Failure。浮动垃圾是指并发清除阶段由于用户线程继续运行而产生的垃圾，这部分垃圾只能到下一次 GC 时才能进行回收。由于浮动垃圾的存在，因此需要预留出一部分内存，意味着 CMS 收集不能像其它收集器那样等待老年代快满的时候再回收。如果预留的内存不够存放浮动垃圾，就会出现 Concurrent Mode Failure，这时虚拟机将临时启用 Serial Old 来替代 CMS。
- 标记 - 清除算法导致的空间碎片，往往出现老年代空间剩余，但无法找到足够大连续空间来分配当前对象，不得不提前触发一次 Full GC。

### G1收集器

> G1收集器在JDK 9中已经成为了默认收集器，正式取代了CMS

G1收集器(Grabge Firest)它是一款面向服务端应用的垃圾收集器，在多 CPU 和大内存的场景下有很好的性能。

#### G1 Region区域

在之前的垃圾回收器中，垃圾分代都是按照下图思路分配，老年代，年轻代都是一整块内存，收集时也是针对整块内存进行收集。

![](https://s2.ax1x.com/2020/02/20/3eaCGt.png)

但是在G1中，这个分区概念被摒弃了。在G1中引入了一种新的概念——Region，简单来说，Region就是将整个内存划分为一小块一小块的区域，每次垃圾回收都只回收一小块区域，避免扫描整个老年代或者年轻代，这样就能够减少STW的时间，达到低时延的目的。同时维护一个优先级列表，首先回收价值高的Region。

![](https://s2.ax1x.com/2020/02/21/3nQGOs.png)

#### 跨代引用问题

虽然看起来Region非常美好，但是存在一种问题，当我们要回收一个Eden中的对象时，还有其他Region中的对象引用了该对象，这样就出现了跨代引用问题。

![](https://s2.ax1x.com/2020/02/21/3n1p8O.png)

#### Remembered Set & Card

为了解决这个问题，G1中引入了两种概念 : Card 和 Remembered Set

- Card : 将每2M的区域分为512份，每份都称为一个Card
- Remembered Set : 每个Region维护一个set，其中能够储存其他内存对象的地址。

解决这种跨代引用的问题实际上思路很简单，我们只需要直到这个对象是否被别的对象引用，是否需要收集的问题。而G1中的Card实际上就是内存地址，当这个内存地址指向了其他区域Region中的对象时，将把这个Card存入引用对象的Region中的Remembered Set中。

![](https://s2.ax1x.com/2020/02/21/3n3wff.png)

每一次回收Region中对象时，首先查找Remembered Set，寻找其中被引用的对象，这一部分对象不会被回收。

这实际上是一种空间换时间的策略，只需要通过维护一个小的set，记录其他区域相互引用信息，避免了多个Region同时扫描，减少了时间消耗。

#### Write Barrier

> A write barrier in a [garbage collector](https://en.wikipedia.org/wiki/Garbage_collection_(computer_science)) is a fragment of code emitted by the compiler immediately before every store operation to ensure that (e.g.) generational invariants are maintained. ——摘自维基百科

简而言之，Write Barrier实际上会让JVM为我们注入一段代码，下面用伪代码来模拟下这个过程。

假如现在有一个类Templete

```java
class Templete{
	Demo demo;
}
```

当我们实例化时，其中的Demo对象是null，然而在我们改变这个Demo对象时

```java
Templete t = new Templete();
t.demo = new Demo();
```

虚拟机会将这个此时templet这个Card标记为Dirty，并且将这个Card放入一个**Dirty Card Queue**。

看起来很难理解，为什么不把这个更新操作后的结果直接写入Remembered Set，而是要先写入一个队列，这就是一个效率的问题，由于Java是多线程应用居多，并且这种对象引用变更的操作非常频繁，所以如果直接写入Remembered Set，很多时候会出现多线程竞争写入，这就需要使用互斥锁来保证安全，时间消耗很大。

所以G1引入了Dirty Card Queue，先来看一张结构图。

![](https://s2.ax1x.com/2020/02/21/3nJQf0.png)

这四个阈值对应四种状态 : 

- White : 此时对象之间依赖操作更新很慢，G1不会做任何处理
- Green : 此时依赖更新变多，此时Refinement线程被激活，将Dirty Card更新到Remembered Set中
- Yellow : 应用产生的更新操作比较多，所有的Refinement线程都被激活，将Dirty Card更新如Remembered Set
- Red : 此时应用线程产生的修改操作太多，应用线程也将参与到队列中Dirty Card写入Remembered Set的操作，此时触发STW，应用停顿。

### G1 GC流程

在G1中主要就是两种GC流程，Fully young GC和Mixed GC 

#### Fully Young GC

在Fully Young GC中是如下流程

- STW 开始停顿
  - 构建Eden 和 Survivor
  - 扫描GC Roots
  - Update RS : 排空所有的Dirty Queue，更新Remembered Set
  - Process RS : 找到哪些对象被老年代对象所引用
  - Object Copy : 把Eden和Survivor中活对象拷贝到另一块Survivor区域中(类似于from和to)
  - Re'ference Processing : 处理除软，弱，幽灵引用的流程

在G1中，回收算法是Mark-Copy算法，将Eden和Survivor存活对象拷贝到另一个Survivor中，年龄增加。

#### Old GC

当堆在达到一定程度时会触发Old GC，需要注意的是Old GC是并发进行的。

为了防止在并发标记阶段出现对象状态变更的情况，G1使用的是**三色标记算法**

#### 三色算法

G1引入了三色算法来完成并发标记,对象将被分为三个颜色，黑，灰，白，同时维护一个队列来存放对象。我做了一套图来表示这个过程。

在开始的时候，队列为空，所有的对象都是白色的，此时开始标记。

![](https://s2.ax1x.com/2020/02/21/3ndifs.png)

首先会将GC Root根节点标记为黑色，因为他们是一定不会回收的，然后将GC Root根所引用的对象标记为灰色，并将他们放入到一个队列中。

![](https://s2.ax1x.com/2020/02/21/3nd311.png)

此时，依次处理队列中的灰色节点，分为两步 : 

1. 将队列中的灰色节点染黑
2. 将黑色节点指向的节点变为灰色，并存入队列

整个过程处理完成后会成为如下结构。

![](https://s2.ax1x.com/2020/02/21/3ndgHS.png)

重复上一步，继续处理队列中的灰色节点，直到队列中再无灰色节点，队列为空时，我们的标记就完成了。

![](https://s2.ax1x.com/2020/02/21/3nd7uV.png)

此时标记状态如图，这些白色的节点应该是被收集的垃圾。

但是! 由于这个阶段是并发执行，应用线程随时可能修改就会出现一个重大的问题**Lost Object Problem**

#### Lost Object Problem

什么是对象丢失 ? 由于并发阶段，随时会出现应用线程修改对象引用的问题，所以会出现如下状况。

![](https://s2.ax1x.com/2020/02/21/3nwJ8s.png)

当我们已经将A标记为黑色时(表示A已经从队列中弹出)，此时应用线程修改了A，将A指向了C`  A.C = C`，于此同时在队列中的B还是灰色状态，但是此时应用线程又将B本来指向A的引用断开了，即`  B.C = null`，此时队列处理B，B染色后发现没有指向任何数据，B弹出，整个过程执行完成。

虽然此时A引用着C，C是一个活着的对象，但是由于应用线程并发修改，C的颜色还是白色，C会被回收，这样就造成了对象丢失的问题，为了避免这种问题，G1使用了如下策略。

G1仍使用Write Barrier来处理，当还处在队列中的元素(B)修改内部引用时，G1仍然认为被修改的对象是一个活得对象，就像图中，当B还是灰色状态执行`  B.C = null`，虚拟机会此时将C染成黑色，认为它是一个获得对象来防止丢失。

![](https://s2.ax1x.com/2020/02/21/3n0NJH.png)

就像这样，即时A并没有引用C，但是当B还在队列中，并且删除了引用，G1依旧会将C认为是一个存活的对象，并不参与此次GC。虽然它已经是一个垃圾。

这样被留到下一次GC的垃圾成为**浮动垃圾**

所以G1整个Old GC流程可以看作如下几个步骤 : 

1. STW ： 首先进行一次Fully Young GC
2. 恢复应用线程
3. 并发标记 : 三色算法
4. STW : 再次停止应用线程
   - 重新标记 : 保证找到的对象都是活对象
   - 回收全空的区域(Clean Up)
5. 恢复应用线程

### Mixed GC

Mixed GC是另一种GC形式，具有如下几个特点

- 不一定立即发生
- 选择若干个Region进行
  - 默认1/8的Old Region
    - `  -xx:G1MixedGCCountTarget=N`
  - Eden+Survivor Region
  - 会触发STW
- 根据暂停目标，会选择垃圾最多的Region进行回收(Grabge First)

### 总结

G1的出现最大的期望就是让用户尽可能少的进行JVM调优，能够自动化的完成对于GC时间的优化，所有也就有了GC优先级的收集策略，G1中最重要的参数是`  -xx:MaxGCPauseMilis=N`，该参数指应用程序每次GC停顿时间，G1会尽量接近这个时间，但不保证一定在该时间内完成。

同时如果这个时间过小，会让系统的吞吐量下滑，因为该值越小，G1会动态调整Region分配的策略，让每个Region尽量的小，这样才能在短时间内回收一个Region，这样会让GC更加频繁的发生，降低系统吞吐量。

### ZGC & Shenandoah

> ZGC在JDK 11中已经能够使用

#### 指针染色

在G1中将对象引用信息存放在RS中，而在ZGC中更进了一步，直接将这部分内容存储到了引用中，但是如果这部分指针变化会造成对象丢失，所以使用到了虚拟内存，将GC信息直接写到了虚拟内存。

因此ZGC不能使用压缩指针，所以会使内存占用比较大，所以ZGC更加适合小堆。

#### 对象头储存

Shenandoah GC也是通过额外引用信息来处理GC，在原有的对象头(Mark Word)添加了一个指针，默认情况下指向自己，如果发生拷贝，指向新的对象，在并发编程时为了保证不丢失写入，能够根据指针跳转到新的对象。



## 类加载

### 类加载的7个过程

#### 加载

将一个Class文件通过类加载器加载进入虚拟机，并解析成为字节码供JVM进行执行

- 通过类全限定名称加载获得二进制字节流
- 将Class中的静态变量存储到永久代(元空间/方法区)
- 在栈中生成一个Main函数入口

可以从多个地方加载Class文件

1. 从ZIP/JAR包等形式加载
2. 从网络中加载字节流
3. 通过JSP等技术加载

#### 验证

对Class文件进行安全验证，防止危害虚拟机

#### 准备

准备阶段时进行符号引用和变量初值(常量直接赋值)，这个阶段static的常量都会被赋值为各自类型的初值，而其名称并不是变量名称而是符号引用。

#### 解析

将符号引用替换为真正引用

#### 初始化

初始化会执行静态代码块等内容

#### 使用

类在虚拟机运行

#### 卸载

通过ClassLoader可以卸载一个类，但是实际中非常少会出现到这种情况

### 类初始化时机

#### 主动引用

虚拟机规范中并没有强制约束何时进行加载，但是规范严格规定了有且只有下列五种情况必须对类进行初始化（加载、验证、准备都会随之发生）：

- 遇到 new、getstatic、putstatic、invokestatic 这四条字节码指令时，如果类没有进行过初始化，则必须先触发其初始化。最常见的生成这 4 条指令的场景是：使用 new 关键字实例化对象的时候；读取或设置一个类的静态字段（被 final 修饰、已在编译期把结果放入常量池的静态字段除外）的时候；以及调用一个类的静态方法的时候。
- 使用 java.lang.reflect 包的方法对类进行反射调用的时候，如果类没有进行初始化，则需要先触发其初始化。
- 当初始化一个类的时候，如果发现其父类还没有进行过初始化，则需要先触发其父类的初始化。
- 当虚拟机启动时，用户需要指定一个要执行的主类（包含 main() 方法的那个类），虚拟机会先初始化这个主类；
- 当使用 JDK 1.7 的动态语言支持时，如果一个 java.lang.invoke.MethodHandle 实例最后的解析结果为 REF_getStatic, REF_putStatic, REF_invokeStatic 的方法句柄，并且这个方法句柄所对应的类没有进行过初始化，则需要先触发其初始化；

#### 被动引用

以上 5 种场景中的行为称为对一个类进行主动引用。除此之外，所有引用类的方式都不会触发初始化，称为被动引用。被动引用的常见例子包括：

- 通过子类引用父类的静态字段，不会导致子类初始化。

```java
System.out.println(SubClass.value);  // value 字段在 SuperClass 中定义Copy to clipboardErrorCopied
```

- 通过数组定义来引用类，不会触发此类的初始化。该过程会对数组类进行初始化，数组类是一个由虚拟机自动生成的、直接继承自 Object 的子类，其中包含了数组的属性和方法。

```java
SuperClass[] sca = new SuperClass[10];Copy to clipboardErrorCopied
```

- 常量在编译阶段会存入调用类的常量池中，本质上并没有直接引用到定义常量的类，因此不会触发定义常量的类的初始化。

```java
System.out.println(ConstClass.HELLOWORLD);
```

### 类加载器

Java中提供了三种类加载器用于对Class文件进行加载

- 启动类加载器（Bootstrap ClassLoader）此类加载器负责将存放在 <JRE_HOME>\lib 目录中的，或者被 -Xbootclasspath 参数所指定的路径中的，并且是虚拟机识别的（仅按照文件名识别，如 rt.jar，名字不符合的类库即使放在 lib 目录中也不会被加载）类库加载到虚拟机内存中。启动类加载器无法被 Java 程序直接引用，用户在编写自定义类加载器时，如果需要把加载请求委派给启动类加载器，直接使用 null 代替即可。
- 扩展类加载器（Extension ClassLoader）这个类加载器是由 ExtClassLoader（sun.misc.Launcher$ExtClassLoader）实现的。它负责将 <JAVA_HOME>/lib/ext 或者被 java.ext.dir 系统变量所指定路径中的所有类库加载到内存中，开发者可以直接使用扩展类加载器。
- 应用程序类加载器（Application ClassLoader）这个类加载器是由 AppClassLoader（sun.misc.Launcher$AppClassLoader）实现的。由于这个类加载器是 ClassLoader 中的 getSystemClassLoader() 方法的返回值，因此一般称为系统类加载器。它负责加载用户类路径（ClassPath）上所指定的类库，开发者可以直接使用这个类加载器，如果应用程序中没有自定义过自己的类加载器，一般情况下这个就是程序中默认的类加载器。

### 双亲委派模型

![](https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/0dd2d40a-5b2b-4d45-b176-e75a4cd4bdbf.png)

加载类时会交由上一层类加载器执行，只有无法执行时，才能使用奔雷加载，这样做是为了避免在同一个应用程序中出现两个相同类别和前缀的类造成系统错误。