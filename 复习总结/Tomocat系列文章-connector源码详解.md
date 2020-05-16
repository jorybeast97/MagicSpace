Tomcat如果仅从高度抽象层面来说,其重点需要理解的组件可以归纳为**connector**和**container**,正是这两个组件完成了HTTP连接建立,请求接收,对象封装以及返回响应的全过程。

本文将从源码角度来简析**connector**在tomcat启动后的加载流程与运行细节,同样,本文基于`  Apache-tomcat-9.0.20-src`源码版本,其他版本可能在细节实现上有所差异,但是总体流程大致相同。

作者将从一下几个过程分析connector工作原理

- connector基本类型与配置
- connector构造器
- Acceptor端口监听
- Poller与Worker线程

该文仅从作者角度阐述,细节把控可能有所欠缺,如果有不足,错误之处,请及时指出。

## Connector基本类型与配置

Connector是Tomcat中的重要组件,我们在前文中提到过,其作用主要负责创建Socket连接监听请求,同时将请求封装为指定对象,而在Tomcat官方文档中,给出了Connector所支持的多种协议。

- Http Connector：解析HTTP请求，又分为BIO Http Connector和NIO Http Connector，即阻塞IO Connector和非阻塞IO Connector。本文主要分析NIO Http Connector的实现过程。
- AJP：基于AJP协议，用于Tomcat与HTTP服务器通信定制的协议，能提供较高的通信速度和效率。如与Apache服务器集成时，采用这个协议。
- APR HTTP Connector：用C实现，通过JNI调用的。主要提升对静态资源（如HTML、图片、CSS、JS等）的访问性能。

本文重点将放在HTTP Connector上,这也是日常开发中我们所使用最多的协议,Tomcat8 Doc中给出了HTTP Conncetor的详细作用概述。

> The **HTTP Connector** element represents a **Connector** component that supports the HTTP/1.1 protocol. It enables Catalina to function as a stand-alone web server, in addition to its ability to execute servlets and JSP pages. A particular instance of this component listens for connections on a specific TCP port number on the server. One or more such **Connectors** can be configured as part of a single [Service](https://tomcat.apache.org/tomcat-8.5-doc/config/service.html), each forwarding to the associated [Engine](https://tomcat.apache.org/tomcat-8.5-doc/config/engine.html) to perform request processing and create the response.

而在`Tomcat 8.5 / 9`版本中,又增加了对于HTTP2.0的支持,这也是一个新的特性变化。同样在文档中给出了一些请求IO的模型。

> Each incoming request requires a thread for the duration of that request. If more simultaneous requests are received than can be handled by the currently available request processing threads, additional threads will be created up to the configured maximum (the value of the `maxThreads` attribute). If still more simultaneous requests are received, they are stacked up inside the server socket created by the **Connector**, up to the configured maximum (the value of the `acceptCount` attribute). Any further simultaneous requests will receive "connection refused" errors, until resources are available to process them.

每个请求进入后,都会创建一个新的线程来处理该请求,同时,Tomcat也能够配置最大线程数量`maxThreads`,当请求传入后,如果超过当前任务线程,则会创建新的线程,当任务线程数量到达`maxThreads`后,服务器会拒绝执行请求,返回"connection refused"。

从这段文字来看,似乎Tomcat使用的请求方式是BIO结合线程池的方式,而到底是否是这样,我们待会从源码中进行探寻。

上述内容简单阐述了三种常见的Connector,Tomcat为我们提供了`  server.xml`文件供我们来配置自己所需要的协议。

```xml
<Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />
```

## Connector构造器

前一篇文章中讲到Tomcat如何启动,而本文则顺着脉络来讲解,Tomcat启动后,当出现了第一个请求,我们应该如何初始化一个Connector的过程。而这个过程中,构造函数则是重中之重。

构造器位于`  org.apache.catalina.connector.Connector`下，我们先来观察一下构造函数外部特征。

```java
public Connector(String protocol) {
    //详细代码 . . . . . . 
}
```

能够注意到,该构造函数是拥有一个参数`  protocol`,特指协议,那么我们来看看谁调用了这个方法并且传入了什么内容。

![](https://s1.ax1x.com/2020/05/15/Yr0l4O.png)

能够发现,Tomcat在创建该对象是,默认使用的是`  HTTP/1.1`的协议,我们再来翻看构造器内部结构。

```java
public Connector(String protocol) {
    boolean aprConnector = AprLifecycleListener.isAprAvailable() &&
        AprLifecycleListener.getUseAprConnector();

    if ("HTTP/1.1".equals(protocol) || protocol == null) {
        if (aprConnector) {
            protocolHandlerClassName = "org.apache.coyote.http11.Http11AprProtocol";
        } else {
            protocolHandlerClassName = "org.apache.coyote.http11.Http11NioProtocol";
        }
    } else if ("AJP/1.3".equals(protocol)) {
        if (aprConnector) {
            protocolHandlerClassName = "org.apache.coyote.ajp.AjpAprProtocol";
        } else {
            protocolHandlerClassName = "org.apache.coyote.ajp.AjpNioProtocol";
        }
    } else {
        protocolHandlerClassName = protocol;
    }

    // Instantiate protocol handler
    ProtocolHandler p = null;
    try {
        Class<?> clazz = Class.forName(protocolHandlerClassName);
        p = (ProtocolHandler) clazz.getConstructor().newInstance();
    } catch (Exception e) {
        log.error(sm.getString(
            "coyoteConnector.protocolHandlerInstantiationFailed"), e);
    } finally {
        this.protocolHandler = p;
    }

    // Default for Connector depends on this system property
    setThrowOnFailure(Boolean.getBoolean("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE"));
}
```

首先来看第一段。

```java
boolean aprConnector = AprLifecycleListener.isAprAvailable() &&
        AprLifecycleListener.getUseAprConnector();
```

apr(Apache Portable Runtime/Apache可移植运行时)，是Apache HTTP服务器的支持库。你可以简单地理解为，Tomcat将以JNI的形式调用Apache HTTP服务器的核心动态链接库来处理文件读取或网络传输操作，从而大大地提高Tomcat对静态文件的处理性能。 Tomcat apr也是在Tomcat上运行高并发应用的首选模式。如果我们的Tomcat不是在apr模式下运行，在启动Tomcat的时候，我们可以在日志信息中看到类似如下信息

```
2013-8-6 16:17:49 org.apache.catalina.core.AprLifecycleListener init
信息: The APR based Apache Tomcat Native library which allows optimal performance in production environments was not found on the java.library.path: xxx/xxx(这里是路径信息)
```

从Tomcat 7.0.30版本开始，Tomcat已经自带了`tcnative-1.dll`等文件，并且默认就是在Tomcat apr模式下运行。

然后就是对于协议的解析部分,该部分使用了，并为`  protocolHandlerClassName`进行赋值,该部分没有什么太多细节内容,就不做详细解释,接着向下看。

```java
ProtocolHandler p = null;
try {
    Class<?> clazz = Class.forName(protocolHandlerClassName);
    p = (ProtocolHandler) clazz.getConstructor().newInstance();
} catch (Exception e) {
    log.error(sm.getString(
        "coyoteConnector.protocolHandlerInstantiationFailed"), e);
} finally {
    this.protocolHandler = p;
}
```

这部分内容才是真正构造器的重点,通过反射的方式来实例化一个`ProtocolHandler`,这个拦截器能够拦截监听指定的协议。

而最后的内容则是当没有进行任何设置时，会使用默认值进行连接器设置。

## Acceptor端口监听

看完构造器的部分,我们发现真正在构造器中进行使用的是`ProtocolHandler`。下面我们来看下这个接口,官方文档中给出的概念是

> Abstract the protocol implementation, including threading, etc. This is the main interface to be implemented by a coyote protocol. Adapter is the main interface to be implemented by a coyote servlet container.

而真正的启动过程在`  org.apache.catalina.connector.Connector#startInternal`方法中。

```java
protected void startInternal() throws LifecycleException {

    // Validate settings before starting
    if (getPortWithOffset() < 0) {
        throw new LifecycleException(sm.getString(
            "coyoteConnector.invalidPort", Integer.valueOf(getPortWithOffset())));
    }

    setState(LifecycleState.STARTING);

    try {
        protocolHandler.start();
    } catch (Exception e) {
        throw new LifecycleException(
            sm.getString("coyoteConnector.protocolHandlerStartFailed"), e);
    }
}
```

注释中非常明确的写道,通过此方法连接器开始处理请求。而我们在看`ProtocolHandler`时发现会有很多的实现类,究竟会使用哪一个实现类,会根据用户配置,例如有BIO/NIO等多种方式,现在来说,一般使用NIO居多,这里我们就以NIO来举例。

![](https://s1.ax1x.com/2020/05/15/YrsMsH.png)

而`  Http11NioProtocol`的代码十分简洁,重点只需要放在一行即可。

```java
public Http11NioProtocol() {
    super(new NioEndpoint());
}
```

**注意** : 在Http11NioProtocol,所有的操作都是委托给NioEndpoint完成的,真正监听端口的时NioEndpoint。

tomcat在使用Http11NioProtocol解析HTTP请求时一共设计了三种线程，分别为Acceptor，Poller和Worker。

Acceptor实现了Runnable接口，根据其命名就知道它是一个接收器，负责接收socket，其接收方法是serverSocket.accept()方式，获得SocketChannel对象，然后封装成tomcat自定义的org.apache.tomcat.util.net.NioChannel。虽然是Nio，但在接收socket时仍然使用传统的方法，使用阻塞方式实现。Acceptor以线程池的方式被创建和管理，在NioEndpoint的startInternal()方法中完成Acceptor的启动

```java
public void startInternal() throws Exception {
    if (!running) {
        running = true;
        paused = false;
        if (socketProperties.getProcessorCache() != 0) {
            processorCache = new SynchronizedStack<>(SynchronizedStack.DEFAULT_SIZE,
                                                     socketProperties.getProcessorCache());
        }
        if (socketProperties.getEventCache() != 0) {
            eventCache = new SynchronizedStack<>(SynchronizedStack.DEFAULT_SIZE,
                                                 socketProperties.getEventCache());
        }
        if (socketProperties.getBufferPool() != 0) {
            nioChannels = new SynchronizedStack<>(SynchronizedStack.DEFAULT_SIZE,
                                                  socketProperties.getBufferPool());
        }
        // Create worker collection
        if (getExecutor() == null) {
            createExecutor();
        }
        initializeConnectionLatch();

        // Start poller threads
        pollers = new Poller[getPollerThreadCount()];
        for (int i = 0; i < pollers.length; i++) {
            pollers[i] = new Poller();
            Thread pollerThread = new Thread(pollers[i], getName() + "-ClientPoller-" + i);
            pollerThread.setPriority(threadPriority);
            pollerThread.setDaemon(true);
            pollerThread.start();
        }

        startAcceptorThreads();
    }
}
```

其注释中非常明确的写道,启动NIO端点，创建Acceptor，轮询器线程。最后会调用`  startAcceptorThreads`来创建接收器线程。

```java
protected void startAcceptorThreads() {
    int count = getAcceptorThreadCount();
    acceptors = new ArrayList<>(count);

    for (int i = 0; i < count; i++) {
        Acceptor<U> acceptor = new Acceptor<>(this);
        String threadName = getName() + "-Acceptor-" + i;
        acceptor.setThreadName(threadName);
        acceptors.add(acceptor);
        Thread t = new Thread(acceptor, threadName);
        t.setPriority(getAcceptorThreadPriority());
        t.setDaemon(getDaemon());
        t.start();
    }
}
```

下面我们来看真正监听端口的Acceptor的`run`方法是如何工作的。由于该方法比较长,所以我直接在代码中做了注释。

```java
public void run() {

    int errorDelay = 0;

    // 等待接收endpoint关闭命令
    while (endpoint.isRunning()) {
        // endpoint阻塞
        while (endpoint.isPaused() && endpoint.isRunning()) {
            state = AcceptorState.PAUSED;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // Ignore
            }
        }

        if (!endpoint.isRunning()) {
            break;
        }
        state = AcceptorState.RUNNING;
        try {
            //连接数到达最大值时，await等待释放connection，在Endpoint的startInterval方法中设置了最大连接数
            endpoint.countUpOrAwaitConnection();

            //等待连接时可能已经被暂停,如果被暂停则直接跳出
            if (endpoint.isPaused()) {
                continue;
            }
            //U是一个socketChannel
            U socket = null;           
            try {
                //接收socket请求
                socket = endpoint.serverSocketAccept();
            } catch (Exception ioe) {
                // 未获取到
                endpoint.countDownConnection();
                if (endpoint.isRunning()) {
                    errorDelay = handleExceptionWithDelay(errorDelay);
                    throw ioe;
                } else {
                    break;
                }
            }
            // 成功获取,重置错误延时
            errorDelay = 0;

            // Configure the socket
            if (endpoint.isRunning() && !endpoint.isPaused()) {
                // endpoint的setSocketOptions方法对socket进行配置
                if (!endpoint.setSocketOptions(socket)) {
                    endpoint.closeSocket(socket);
                }
            } else {
                endpoint.destroySocket(socket);
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            String msg = sm.getString("endpoint.accept.fail");
            // APR specific.
            // Could push this down but not sure it is worth the trouble.
            if (t instanceof Error) {
                Error e = (Error) t;
                if (e.getError() == 233) {
                    // Not an error on HP-UX so log as a warning
                    // so it can be filtered out on that platform
                    // See bug 50273
                    log.warn(msg, t);
                } else {
                    log.error(msg, t);
                }
            } else {
                log.error(msg, t);
            }
        }
    }
    state = AcceptorState.ENDED;
}

```

到这里可能会有些乱,我们先暂停一下,想一个问题。

> 在NIO中,当我们接收到了一个IO请求后,下一步该做什么?

答案很简单，我们需要将其注册进入一个队列中,由一个线程去监控IO请求的状态。如果能思考到这里后面就很容易理解。在run方法中,Acceptor已经接收到了一个请求,现在需要Endpoint将其注册进入一个队列中等待被轮询,而这个方法就是`  setSocketOptions`.

该方法很长,我只展示了其中最需要关注的一行。

```java
getPoller0().register(channel);
```

顺着`  getPoller0`调用链向上摸索,能够找到一个Endpoint的内部类Poller。

## Poller与Worker线程

Pollor同样实现了Runnable接口，是NioEndpoint类的内部类。在Endpoint的startInterval方法中创建、配置并启动了Pollor线程，见代码清单4。Poolor主要职责是不断轮询其selector，检查准备就绪的socket(有数据可读或可写)，实现io的多路复用。其构造其中初始化了selector。

```java
public Poller() throws IOException {
    this.selector = Selector.open();
}
```

其构造方法中就开启了`  Selector.open()`。而Poller的核心代码也在其` run `方法中。

> ```
> The background thread that adds sockets to the Poller, checks the
> poller for triggered events and hands the associated socket off to an
> appropriate processor as events occur.
> ```
>
> 注释中这样描述 : 向套接字添加套接字的后台线程，在轮询器中检查触发事件，并在事件发生时将关联的套接字交给适当的处理器。

由此可见,这是一个典型的事件触发机制的IO模型。我们来看一下其实现方式。

```java
public void run() {
    // 调用了destroy()方法后终止此循环
    while (true) {
        boolean hasEvents = false;
        try {
            if (!close) {
                hasEvents = events();
                if (wakeupCounter.getAndSet(-1) > 0) {
                    //if we are here, means we have other stuff to do
                    //非阻塞的 select
                    keyCount = selector.selectNow();
                } else {
                    //阻塞selector，直到有准备就绪的socket
                    keyCount = selector.select(selectorTimeout);
                }
                wakeupCounter.set(0);
            }
            if (close) {
                //该方法遍历了eventqueue中的所有PollorEvent，然后依次调用PollorEvent的run，将socket注册到selector中。
                events();  
                timeout(0, false);
                try {
                    selector.close();
                } catch (IOException ioe) {
                    log.error(sm.getString("endpoint.nio.selectorCloseFail"), ioe);
                }
                break;
            }
        } catch (Throwable x) {
            ExceptionUtils.handleThrowable(x);
            log.error("", x);
            continue;
        }
        //either we timed out or we woke up, process events first
        if (keyCount == 0) hasEvents = (hasEvents | events());

        Iterator<SelectionKey> iterator =
            keyCount > 0 ? selector.selectedKeys().iterator() : null;
        // 遍历就绪的socket
        while (iterator != null && iterator.hasNext()) {
            SelectionKey sk = iterator.next();
            NioSocketWrapper attachment = (NioSocketWrapper) sk.attachment();
            // Attachment may be null if another thread has called
            // cancelledKey()
            if (attachment == null) {
                iterator.remove();
            } else {
                //调用processKey方法对有数据读写的socket进行处理，在分析Worker线程时会分析该方法
                iterator.remove();
                processKey(sk, attachment);
            }
        }
        //process timeouts
        timeout(keyCount, hasEvents);
    }//while

    getStopLatch().countDown();
}

```

而Worker线程即SocketProcessor是用来处理Socket请求的。SocketProcessor也同样是Endpoint的内部类。在Pollor的run方法中(代码清单8)监听到准备就绪的socket时会调用processKey方法进行处理

```java
protected void processKey(SelectionKey sk, NioSocketWrapper attachment) {
    try {
        if (close) {
            cancelledKey(sk);
        } else if (sk.isValid() && attachment != null) {
            //有读写事件就绪时
            if (sk.isReadable() || sk.isWritable()) {
                if (attachment.getSendfileData() != null) {
                    processSendfile(sk, attachment, false);
                } else {
                    unreg(sk, attachment, sk.readyOps());
                    boolean closeSocket = false;
                    // socket可读时，先处理读事件
                    if (sk.isReadable()) {
                        //调用processSocket方法进一步处理
                        if (!processSocket(attachment, SocketEvent.OPEN_READ, true)) {
                            closeSocket = true;
                        }
                    }
                    //写事件
                    if (!closeSocket && sk.isWritable()) {
                        //调用processSocket方法进一步处理
                        if (!processSocket(attachment, SocketEvent.OPEN_WRITE, true)) {
                            closeSocket = true;
                        }
                    }
                    if (closeSocket) {
                        cancelledKey(sk);
                    }
                }
            }
        } else {
            //invalid key
            cancelledKey(sk);
        }
    } catch (CancelledKeyException ckx) {
        cancelledKey(sk);
    } catch (Throwable t) {
        ExceptionUtils.handleThrowable(t);
        log.error("", t);
    }
}

```

继续跟踪运行轨迹

```java
public boolean processSocket(SocketWrapperBase<S> socketWrapper,
                             SocketEvent event, boolean dispatch) {
    try {
        if (socketWrapper == null) {
            return false;
        }
        // 尝试循环利用之前回收的SocketProcessor对象，如果没有可回收利用的则
        // 创建新的SocketProcessor对象
        SocketProcessorBase<S> sc = processorCache.pop();
        if (sc == null) {
            创建SocketProcessor，即Worker线程，基于线程池模式进行创建和管理
                sc = createSocketProcessor(socketWrapper, event);
        } else {
            // 循环利用回收的SocketProcessor对象
            sc.reset(socketWrapper, event);
        }
        Executor executor = getExecutor();
        if (dispatch && executor != null) {
            //SocketProcessor实现了Runneble接口，可以直接传入execute方法进行处理
            executor.execute(sc);
        } else {
            sc.run();
        }
    } catch (RejectedExecutionException ree) {
        getLog().warn(sm.getString("endpoint.executor.fail", socketWrapper) , ree);
        return false;
    } catch (Throwable t) {
        ExceptionUtils.handleThrowable(t);
        getLog().error(sm.getString("endpoint.process.fail"), t);
        return false;
    }
    return true;
}

//NioEndpoint中createSocketProcessor创建一个SocketProcessor。
protected SocketProcessorBase<NioChannel> createSocketProcessor(
    SocketWrapperBase<NioChannel> socketWrapper, SocketEvent event) {
    return new SocketProcessor(socketWrapper, event);
}


```

至此,基本上已经分析完了Http11Nio'Protocol的运行流程。

## 总结

![](https://s1.ax1x.com/2020/05/15/YrR2Sx.png)

我将整个流程按照一张流程图的形式进行总结。Http11NioProtocol是基于Java Nio实现的，创建了Acceptor、Pollor和Worker线程实现多路io的复用。Acceptor和Pollor之间是生产者消费者模式的关系，Acceptor不断向EventQueue中添加PollorEvent，Pollor轮询检查EventQueue中就绪的PollorEvent，然后发送给Work线程进行处理。

## 参考文章

- [Tomcat-8.5-doc](https://tomcat.apache.org/tomcat-8.5-doc/config/http.html)
- [Tomcat优化 : APR协议](https://my.oschina.net/u/2321834/blog/1491515)
- [Tomcat源码分析](https://juejin.im/post/5af27c34f265da0b78687e14)