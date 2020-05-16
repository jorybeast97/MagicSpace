

在使用Spring Boot的过程中,时时刻刻都会使用到Tomcat,但是回顾以往的学习,似乎没有对Tomcat进行一个知识的梳理,最近在回想Tomcat的相关知识,发现很多内容是混乱而没有逻辑的,所以打算以笔记的形式重新总结下Tomcat的学习过程以及它的框架设计思路。

Tomcat组件很复杂,如果直接阅读源码可能会产生无从下手的感觉。好在Apache提供了Tomcat的框架设计图以及对各个模块的Doc,能够辅助我们完成对于各个部分功能的理解,而本文将尽量详尽的记录我所理解的Tomcat的工作流程,如果有不足的地方,请及时指正。

个人认为,Tomcat的学习按照如下方式比较合理 : 

- Tomcat组件设计原理
- 一个Servelt究竟是如何在Tomcat上接收Request请求并返回Respone
- 各个组件源码级理解
- 串联多个组件,搞懂Tomcat运行流程
- 总结

本文将仅从Tomcat背景以及基本结构方面进行总结,源码以及Debug过程将在后续系列文章中进行总结。

<!--more-->

---------------------------

## 什么是Tomcat?

这里引用维基百科的知识和官网文档的知识。

> **Tomcat**是由Apache软件基金会属下[Jakarta项目](https://zh.wikipedia.org/wiki/Jakarta项目)开发的[Servlet](https://zh.wikipedia.org/wiki/Servlet)容器，按照[Sun Microsystems](https://zh.wikipedia.org/wiki/Sun_Microsystems)提供的技术规范，实现了对[Servlet](https://zh.wikipedia.org/wiki/Servlet)和[JavaServer Page](https://zh.wikipedia.org/wiki/JavaServer_Page)（[JSP](https://zh.wikipedia.org/wiki/JSP)）的支持，并提供了作为Web服务器的一些特有功能，如Tomcat管理和控制平台、安全域管理和Tomcat阀等。由于Tomcat本身也内含了[HTTP](https://zh.wikipedia.org/wiki/HTTP)[服务器](https://zh.wikipedia.org/wiki/服务器)，因此也可以视作单独的[Web服务器](https://zh.wikipedia.org/wiki/Web服务器)。但是，不能将Tomcat和[Apache HTTP服务器](https://zh.wikipedia.org/wiki/Apache_HTTP服务器)混淆，[Apache HTTP服务器](https://zh.wikipedia.org/wiki/Apache_HTTP服务器)是用C语言实现的HTTP[Web服务器](https://zh.wikipedia.org/wiki/Web服务器)；这两个HTTP web server不是捆绑在一起的。Apache Tomcat包含了配置管理工具，也可以通过编辑XML格式的配置文件来进行配置。        ——    《维基百科》

简单来说,Tomcat是一个能够接收Http请求并可以放回一个Http响应的应用程序。可以看作为一个Socket程序和一个针对JSP和Servlet的容器。

在了解Tomcat的工作原理之前,先要Servlet有所了解,这有关历史问题,是不能够绕过的一个阶段。

### Servlet技术背景

早期互联网都是大量的静态页面,而后来用户交互需求的增多则出现了动态响应技术。如日中天的SUN公司想搞个事情,于是研发了Applet,希望能够搭配Java使用来改变互联网,然而Applet实际用途不大,并没有按照SUN公司的规划成为主流技术。

但是SUN公司不想放弃这块大蛋糕,于是研发了Servlet技术，Servlet 其实就是 SUN 为了让 Java 能实现动态的可交互的网页, 从而进入 web 编程的领域而定义的一套标准。

简单来描述一下这个标准。

这套标准是这么说的: 你想用 Java 开发动态网页, 可以定义一个自己的" Servlet", 但一定要实现我的 HTTPServlet 接口, 然后重载 `doGet()`, `doPost()`方法. 用户从流浪器 GET 的时候, 调用 `doGet` 方法, 从流浪器向服务器发送表单数据的时候, 调用 `doPost` 方法, 如果你想访问用户从浏览器传递过来的参数, 用 HttpServletRequest 对象就好了, 里面有 getParameter, getQueryString 方法, 如果你处理完了, 想向浏览器返回数据, 用 HttpServletResponse 对象调用 getPrintWriter 方法就可以输出数据了。

### Servlet规范工作职责

如果想要完整的阐述Servlet的工作流程是非常复杂的,但是如果想从Http请求的角度来概括一下Servlet做了什么,这个过程会简化许多,由于本文主要是总结Tomcat相关内容,所以不会讲太多的篇幅花费在Servlet上,这里仅从Servlet职责方法来做个总结性的概述。

Servlet是个很完善而复杂的系统,当时当Http请求进入时,它将专注于三个操作 : 

1. 创建一个 Request 对象, 用可能会在调用的 Servlet 中使用到的信息填充该 Request 对象, 如参数, 头, cookie, 查询字符串, URI 等, Request 对象是 javax.servlet.ServletRequest 接口或 javax.servlet.ServletRequest 接口的一个实例.
2. 创建一个调用 Servlet 的 Response 对象, 用来向 Web 客户端发送响应. response 对象是 javax.servlet.http.ServletResponse 接口或 javax.servlet.ServletResponse 接口的一个实例;
3. 调用 Servlet 的 service 方法, 将 request 对象和 response 对象作为参数传入, Servlet 从 request 对象中读取信息, 并通过 response 对象发送响应信息.

这样就能够完成了Http请求处理了吗? 答案是否定的。

Servlet本质上是个应用程序,而Http底层则是一个TCP连接,那么我们怎么去让一个Socket发送数据到Servlet?如果直接在内部维护一个Socket,这样会让程序耦合度极高。所以就出现了容器概念。

Tomcat实际上就是一个Servlet的容器,能够将浏览器发送的Http请求通过一系列链路处理后转发给Servlet,然后再将响应返回给浏览器,这也是Tomcat上所做的最多的操作。

而早先做动态页面时,并没有现在的模板或者前后端分离开发的概念,当时会直接在Servlet中拼接HTML代码,这样的耦合度是无法想象的,并且对于代码的修改和管理也是地狱难度。

因此JSP的出现,这个问题得到了很大的缓解,我们能够通过向HTML中内嵌JAVA代码来完成页面的动态交互。而JSP也非一门新的技术,本质上它就是一个轻量级的Servlet,没有提供任何超出Servlet范围的功能。在执行JSP前,都会将其编译为一个Servlet才能够运行。

在 JSP 中编写静态 HTML 更加方便, 不必再用 println 语句来输出每一行HTML 代码, 更重要的是, 借助内容和外观的分离,页面制作中不同性质的任务可以方便的分开: 比如, 有页面设计者进行 HTML 设计, 同时预留供 Java 程序插入动态内容的空间.

因此Tomcat同样能够运行JSP,因为其本质就是一个轻量级Servlet。

## Tomcat基本结构

只要涉及到Tomcat一定绕不开一个Tomcat的组件—— **Catalina Servlet**,	tomcat 的主体是 Catalina, catalina 是一个成熟的软件, 设计和开发的十分优雅, 功能结构也是模块化的. 我们之前说 `Servlet 是如何工作的?`中提到了 Servlet 容器的任务, 基于这些任务可以将 Catalina 划分为两个模块: **连接器(connector)和容器(container)**.

### Catalina Servlet

关于Catalina的资料很少,我在Google上找到了一篇简短的Catalina的介绍文章。

> [An Introduction to Tomcat Catalina ](https://www.mulesoft.com/tcat/tomcat-catalina)

其中有一些观点能够支撑我们往下理解Tomcat的设计实现 : 

- Tomcat实际上由许多组件组成，包括 [Tomcat JSP](https://www.mulesoft.com/tomcat-jsp) 引擎和各种不同的连接器，但其核心组件称为Catalina。Catalina提供了Tomcat的servlet规范的实际实现。当您 [启动Tomcat服务器时](https://www.mulesoft.com/tomcat-start)，实际上是在启动Catalina。
- Catalina提供了一系列的配置文件,通过这些配置文件能够按照用户需求配置自己所需要的Tomcat方案
- Catalina由Java编写,并且包含在Tomcat源码之中

关于更多的Catalina内容,在后面源码部分能够详细看到其作用和设计,这里只做简单解释。

### Tomcat框架结构

闲言少叙,在上文中提过两个概念**连接器(connector)和容器(container)**,这两个概念将贯穿整个Tomcat的学习过程,因为这两个概念就是我们之前提到了Tomcat的两个主要功能**Socket连接**和**Servlet容器管理**的真正实现。而围绕着这两个概念,设计者又引入了多个模块进行功能上的增强和模块间的解耦,最终整个架构如下图。

![](https://s1.ax1x.com/2020/05/13/Yd968U.gif)

从图中可以看到,Tomcat可以看成一种树形结构,整个服务器作为一个Server存在,然后内部拥有多个服务,每个服务器中又有多个连接器和一个容器,同时配置了一些辅助模块来帮助Service完成更好的操作。

我们来看下最新的源码部分,这是Github上Tomcat源码结构。

![](https://s1.ax1x.com/2020/05/13/YdPqHA.png)

其结构可以看作如下目录 : 

1. Server
   - Service
     - Connetor 
       - HTTP协议
       - AJP协议
     - Container
       - Engine
       - Host
       - Context
       - Wrapper
     - Component
       - Manager管理器
       - logger日志记录
       - loader加载器
       - pipeline管道
       - valve管道阀

*Server*是整个Tomcat组件的容器，包含一个或多个Service。 *Service*：Service是包含Connector和Container的集合，Service用适当的Connector接收用户的请求，再发给相应的Container来处理。

Tomcat的两个核心组件就是Connector和Container,但是这两个组件又构成了Service,在Tomcat中,对外提供服务的真正组件就是Service,但是由于Tomcat中拥有多个Service,所以我们需要个地方来进行管理,这个地方就是Server。

Connector是一个连接器，主要负责接收请求并把请求交给Container，Container就是一个容器，主要装的是具体处理请求的组件。Service主要是为了关联Container与Connector，一个单独的Container或者一个单独的Connector都不能完整处理一个请求，只有两个结合在一起才能完成一个请求的处理

Server这是负责管理Service集合，从图中我们看到一个Tomcat可以提供多种服务，那么这些Serice就是由Server来管理的，具体的工作包括：对外提供一个接口访问Service，对内维护Service集合，维护Service集合又包括管理Service的生命周期、寻找一个请求的Service、结束一个Service等

#### Connector组件

在Tomcat中,真正处理问题的单元是Container,但是容器并没有对外暴露的功能,那么容器从哪里获取信息呢?这就是Connector的作用,Connector我们可以看作一个Socket程序,专门用于接收Http请求并将数据发送给Container的组件。在Tomcat中,存在两种Connector,一种叫http connectoer， 用来传递http需求的。 另一种叫AJP， 在我们整合apache与tomcat工作的时候，apache与tomcat之间就是通过这个协议来互动的。 

官方文档(基于Tomcat 7)中指出

> The **HTTP Connector** element represents a **Connector** component that supports the HTTP/1.1 protocol. It enables Catalina to function as a stand-alone web server, in addition to its ability to execute servlets and JSP pages. A particular instance of this component listens for connections on a specific TCP port number on the server. One or more such **Connectors** can be configured as part of a single [Service](https://tomcat.apache.org/tomcat-7.0-doc/config/service.html), each forwarding to the associated [Engine](https://tomcat.apache.org/tomcat-7.0-doc/config/engine.html) to perform request processing and create the response.

在Tomcat8后,已经完成了对于HTTP 2.0的支持,所以Tomcat Connector能够监控TCP连接端口,并接收HTTP请求,将其转发给和**自己绑定的Engine**处理请求返回响应,也就是说当TCP发送的数据到达Connector时会被封装为一个HttpRequest对象,在之后Container的操作中,都是对这个已经封装好的对象来进行操作处理。

#### Container组件

Container是Tomcat中非常重要的一个内容,但是遗憾的是我在网上并没有找到很有意义的中文资料,所以只能够去挖Tomcat的文档内容,所幸发现了一些比较好的总结性介绍。

> A **Container** is an object that can execute requests received from a client, and return responses based on those requests. A Container may optionally support a pipeline of Valves that process the request in an order configured at runtime, by implementing the **Pipeline** interface as well.
>
> Containers will exist at several conceptual levels within Catalina. The following examples represent common cases:
>
> - **Engine** - Representation of the entire Catalina servlet engine, most likely containing one or more subcontainers that are either Host or Context implementations, or other custom groups.
> - **Host** - Representation of a virtual host containing a number of Contexts.
> - **Context** - Representation of a single ServletContext, which will typically contain one or more Wrappers for the supported servlets.
> - **Wrapper** - Representation of an individual servlet definition (which may support multiple servlet instances if the servlet itself implements SingleThreadModel).

这部分主要阐述了一个容器是一个能够**接收请求,并完成响应的对象**,同时能够通过管道的方式来配置阀门完成对于请求的顺序执行处理。

容器同样存在层级问题,而细分如Engine,Host等容器则代表了不同的层级。

Engine包含了Host和Context,同样能够自定义组来实现。而Host则代表了虚拟主机，Context则指常见的Web上下文,包含了多个Wrapper,而Wrapper则是最细粒度的容器,每一个Wrapper对应一个Servlet,每个Wrapper都能够对自己所负责的Servlet的请求和响应进行管理。

#### Component

需求被传递到了容器里面， 在合适的时候， 会传递给下一个容器处理。而容器里面又盛装着各种各样的组件， 我们可以理解为提供各种各样的增值服务。比如:

1. manager: 当一个容器里面装了manager组件后，这个容器就支持session管理了， 事实上在tomcat里面的session管理, 就是靠的在context里面装的manager component.
2. logger: 当一个容器里面装了logger组件后， 这个容器里所发生的事情， 就被该组件记录下来, 我们通常会在logs/ 这个目录下看见catalina_log.time.txt 以及localhost.time.txt和localhost_examples_log.time.txt。 这就是因为我们分别为：engin, host以及context(examples)这三个容器安装了logger组件， 这也是默认安装， 又叫做标配 .
3. loader: loader这个组件通常只会给我们的context容器使用，loader是用来启动context以及管理这个context的classloader用的。
4. pipline: pipeline是这样一个东西，使用的责任链模式.  当一个容器决定了要把从上级传递过来的需求交给子容器的时候， 他就把这个需求放进容器的管道(pipeline)里面去。 而需求傻呼呼得在管道里面流动的时候， 就会被管道里面的各个阀门拦截下来。 比如管道里面放了两个阀门。 第一个阀门叫做“access_allow_vavle”， 也就是说需求流过来的时候，它会看这个需求是哪个IP过来的， 如果这个IP已经在黑名单里面了，sure, 杀！ 第二个阀门叫做“defaul_access_valve”它会做例行的检查， 如果通过的话，OK， 把需求传递给当前容器的子容器。 就是通过这种方式， 需求就在各个容器里面传递，流动， 最后抵达目的地的了。
5. valve: 就是上面所说的阀门。

主要的三个部分清楚后,我用一张图来描述一下各个部分的结构层次。

![](https://s1.ax1x.com/2020/05/13/YdKFhQ.png)

**注意** : 该图中所有部分都是按照单例出现的,实际上在底层中会有非常多的Connector以及容器。

## 总结

通篇文章简单的讲解了Tomcat中几个重要的组件以及其工作职责,如果不结合源码看可能还有很多不明白的地方,我在之后的文章会尽我所能去完善和总结细节方面的问题,尽量从一个学习者的角度考虑Tomcat的实现流程和学习曲线,尽量保证其难度适中,易于理解。

## 参考资料

- [深入理解Tomcat](https://juejin.im/post/5a59f1c25188257323351a71)
- [An introduction to Tomcat Catalina](https://www.mulesoft.com/tcat/tomcat-catalina)
- [Apache Tomcat Document](http://tomcat.apache.org/)
- [Tomcat resouce code in Github](https://github.com/apache/tomcat/tree/master/java/org/apache)