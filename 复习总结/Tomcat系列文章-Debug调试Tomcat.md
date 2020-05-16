在之前的一篇文章中已经总结了Tomcat的几个重要组件以及其担任的工作职责。而本篇文章将总结如何去以Debug方式运行Tomcat的源码,以及其启动过程中到底做了哪些内容。

本文基于`JDK1.8`以及`tomcat-9.0.20-src`源码版本,不能保证其他版本能够正常运行,本文中已经展示了作者自己在启动的时候会出现的一些BUG以及解决方式。

本文将主要解决如下几个问题

1. 使用IDEA搭建环境运行并调试tomcat源代码
2. 分析`  catalina.bootstrap`的启动过程
3. 分析`catalinHomeFile`和`catalinaBaseFile`的语义以及不可替代性
4. 分析`  common`,`  catalina`,` shared`被加载过程
5. 分析`  catalina`被实例化的过程

本文篇幅较长,需要一个时间段来阅读。

作者代码功力尚浅,如有不足错误之处,请及时指正,感谢阅读。

--------------------------------------

## 资源准备

> **注意 : tomcat版本小版本变化会导致Build过程失败,请严格使用文中的版本**

我们使用的是` apache-tomcat-9.0.20-src`和`  JDK 1.8`,这里仅提供tomcat的下载地址。

apache-tomcat-9.0.20-src下载地址 : http://archive.apache.org/dist/tomcat/

![](https://s1.ax1x.com/2020/05/14/Y0lW5V.png)

之后我们需要使用maven来进行项目编译,这里需要准备两个maven的`  pom.xml`文件。

```xml
<?xml version="1.0" encoding="UTF-8"?>    
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"    
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">    
    
    <modelVersion>4.0.0</modelVersion>    
    <groupId>gxf</groupId>    
    <artifactId>apache-tomcat-9</artifactId>    
    <name>apache-tomcat-9-source</name>    
    <version>1.0</version>    
    <packaging>pom</packaging>    
    
    <modules>    
        <module>apache-tomcat-9.0.20-src</module>    
    </modules>    
</project>
```

```xml
<?xml version="1.0" encoding="UTF-8"?>    
<project xmlns="http://maven.apache.org/POM/4.0.0"    
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"    
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">    
    
    
    <modelVersion>4.0.0</modelVersion>    
    <groupId>org.apache.tomcat</groupId>    
    <artifactId>Tomcat9.0</artifactId>    
    <name>Tomcat9.0</name>    
    <version>9.0</version>    
    
    <build>    
        <finalName>Tomcat9.0</finalName>    
        <sourceDirectory>java</sourceDirectory>    
        <testSourceDirectory>test</testSourceDirectory>    
        <resources>    
            <resource>    
                <directory>java</directory>    
            </resource>    
        </resources>    
        <testResources>    
            <testResource>    
                <directory>test</directory>    
            </testResource>    
        </testResources>    
        <plugins>    
            <plugin>    
                <groupId>org.apache.maven.plugins</groupId>    
                <artifactId>maven-compiler-plugin</artifactId>    
                <version>2.0.2</version>    
    
                <configuration>    
                    <encoding>UTF-8</encoding>    
                    <source>1.8</source>    
                    <target>1.8</target>    
                </configuration>    
            </plugin>    
        </plugins>    
    </build>

    <dependencies>
        <dependency>
            <groupId>ant</groupId>
            <artifactId>ant</artifactId>
            <version>1.7.0</version>
        </dependency>
        <dependency>
            <groupId>ant</groupId>
            <artifactId>ant-apache-log4j</artifactId>
            <version>1.6.5</version>
        </dependency>
        <dependency>
            <groupId>ant</groupId>
            <artifactId>ant-commons-logging</artifactId>
            <version>1.6.5</version>
        </dependency>
        <dependency>
            <groupId>wsdl4j</groupId>
            <artifactId>wsdl4j</artifactId>
            <version>1.6.2</version>
        </dependency>
        <dependency>
            <groupId>javax.xml.rpc</groupId>
            <artifactId>javax.xml.rpc-api</artifactId>
            <version>1.1</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.eclipse.jdt.core.compiler/ecj -->
        <dependency>
            <groupId>org.eclipse.jdt.core.compiler</groupId>
            <artifactId>ecj</artifactId>
            <version>4.4.2</version>
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

然后我们将` apache-tomcat-9.0.20-src`解压,按照以下路径配置。

```
//顶层文件
tomcat
	----  apache-tomcat-9.0.20-src
		---- 其他源码(bin/webapps/conf...)
		---- pom.xml(文中第二个pom)
	----  pom.xml(文中第一个pom)
```

然后IDEA导入项目`  tomcat`,现build以下,一般来说会报第一种错误。

```
Error:(394, 40) java: 找不到符号
  符号:   变量 VERSION_10
  位置: 类 org.eclipse.jdt.internal.compiler.impl.CompilerOptions
Error:(399, 40) java: 找不到符号
  符号:   变量 VERSION_11
  位置: 类 org.eclipse.jdt.internal.compiler.impl.CompilerOptions
Error:(397, 40) java: 找不到符号
  符号:   变量 VERSION_11
  位置: 类 org.eclipse.jdt.internal.compiler.impl.CompilerOptions
```

这是个tomcat为之后JDK预留版本的一个问题,直接找到源代码,将这些版本改为`  jdk 1.8`即可。

![](https://s1.ax1x.com/2020/05/14/Y03nfK.png)

然后需要删除`  test`测试文件,这些文件中报错比较多,一般都是关于`  easyMock`的错误,删除不影响程序运行。

最后需要配置一下启动项。

![](https://s1.ax1x.com/2020/05/14/Y03s7n.png)

Main Class配置为`  catalina.startup.Bootratp`,这就是整个tomcat的运行入口。

第二个参数为指定tomcat默认配置读取路径,如果不配置会找不到conf,配置` apache-tomcat-9.0.20-src`的根目录即可。

此时运行项目。

![](https://s1.ax1x.com/2020/05/14/YBgrcD.png)

## 启动流程

上述启动过程中已经了解到,tomcat真正的程序入口`  org.apache.catalina.startup.Bootstrap`这个类。而该类的启动过程主要做了如下几件事情 : 

- 通过静态代码块加载`catalinaHomeFile`和`catalinaBaseFile`两个路径
- 加载了`  common`,`  catalina`,`  shared`三个组件
- 创建`  catalina`对象

下面我们进行逐条分析。

### 静态代码块

当一个类被加载进入虚拟机后,它的静态代码块中代码会被执行,所以在tomcat的设计中,静态代码块中的部分是重点,也是很多之后操作的基础。

static中内容有些多,但是逻辑分明,所以我们按照一段一段来处理。

```java
static {
        // Will always be non-null
        String userDir = System.getProperty("user.dir");

        // Home first
        String home = System.getProperty(Globals.CATALINA_HOME_PROP);
        File homeFile = null;

        if (home != null) {
            File f = new File(home);
            try {
                homeFile = f.getCanonicalFile();
            } catch (IOException ioe) {
                homeFile = f.getAbsoluteFile();
            }
        }

       //其余内容
        . . . . . .
    }
```

首先会判断home目录是否已经被加载过。由于tomcat可能会在一台机器上产生多个实例,为了防止每次都去要创建一个副本,程序希望能够使用公共变量来进行初始化加载尽量节约内存。(如果在每个线程中维护一个Threadlocal是可行的,但是意义不大,因为这些变量只在启动阶段有作用)。

如果home不为空,说明此时已经被加载过,直接使用IO操作来获取到`  homefile`即可。

```java
if (homeFile == null) {
    // First fall-back. See if current directory is a bin directory
    // in a normal Tomcat install
    File bootstrapJar = new File(userDir, "bootstrap.jar");

    if (bootstrapJar.exists()) {
        File f = new File(userDir, "..");
        try {
            homeFile = f.getCanonicalFile();
        } catch (IOException ioe) {
            homeFile = f.getAbsoluteFile();
        }
    }
}
```

如果为空,则说明tomcat是第一次启动,我们需要加载一些类。这里可以看到会主动按照路径加载` bootstrap.jar `。如果能够加载到,就会像上面一样继续执行。

```java
if (homeFile == null) {
    // Second fall-back. Use current directory
    File f = new File(userDir);
    try {
        homeFile = f.getCanonicalFile();
    } catch (IOException ioe) {
        homeFile = f.getAbsoluteFile();
    }
}
```

由于按照路径加载可能会无法找到` bootstrap.jar `,那么这次一会根据当前目录进行加载。这样做了两次检查,确保这个` bootstrap.jar `能够被加载进入JVM中。

```java
static {
    //前面代码 . . . . . .
    catalinaHomeFile = homeFile;
    System.setProperty(
        Globals.CATALINA_HOME_PROP, catalinaHomeFile.getPath());

    // Then base
    String base = System.getProperty(Globals.CATALINA_BASE_PROP);
    if (base == null) {
        catalinaBaseFile = catalinaHomeFile;
    } else {
        File baseFile = new File(base);
        try {
            baseFile = baseFile.getCanonicalFile();
        } catch (IOException ioe) {
            baseFile = baseFile.getAbsoluteFile();
        }
        catalinaBaseFile = baseFile;
    }
    System.setProperty(
        Globals.CATALINA_BASE_PROP, catalinaBaseFile.getPath());
}

```

#### catalinaHomeFile 和 catalinaBaseFile

当两次加载完成后,此时就能够获取到`homeFile`,这里就开始进行我们之前提到的`  catalinaHomeFile`和`  catalinaBaseFile`。

在stackOverflow上,有一个问题是关于catalinaHomeFile和catalinaBaseFile是否能够只存在一个的必要性。其中给出了这两个变量的作用。我们这里引用一下。

> `CATALINA_HOME` represents the root of your Tomcat installation.
>
> Optionally, Tomcat may be configured for multiple instances by defining `$CATALINA_BASE` for each instance. If multiple instances are not configured, `$CATALINA_BASE` is the same as `$CATALINA_HOME`.

catalinaHomeFile是Tomcat的安装目录,如果你只希望运行一个Tomcat实例时,catalinaHomeFile和catalinaBaseFile是相同的,但是如果你希望运行多个Tomcat实例,则需要catalinaBaseFile变量。

这里为了方便大家,我同样将这两个配置文件在多实例下的区别一并引用。

> In many circumstances, it is desirable to have a single copy of a Tomcat binary distribution shared among multiple users on the same server. To make this possible, you can set the `CATALINA_BASE` environment variable to the directory that contains the files for your 'personal' Tomcat instance.
>
> (翻译 : 很多情况下,用户希望在机器上运行多个tomcat实例,为此,可以将CatalinaBaseFile设置为你单独的实例空间)
>
> When running with a separate `CATALINA_HOME` and `CATALINA_BASE`, the files and directories are split as following:
>
> In `CATALINA_BASE`:
>
> - `bin` - Only: setenv.sh (*nix) or setenv.bat (Windows), tomcat-juli.jar
> - `conf` - Server configuration files (including server.xml)
> - `lib` - Libraries and classes, as explained below
> - `logs` - Log and output files
> - `webapps` - Automatically loaded web applications
> - `work` - Temporary working directories for web applications
> - `temp` - Directory used by the JVM for temporary files>
>
> In `CATALINA_HOME`:
>
> - `bin` - Startup and shutdown scripts
> - `lib` - Libraries and classes, as explained below
> - `endorsed` - Libraries that override standard "Endorsed Standards". By default it's absent.

到此,我们基本上就将static内部所有的过程都完成了,那下面我们就来看一看在加载完成后,`  bootstrap`会做些什么。

### main函数

由于`  org.apache.catalina.startup.Bootstrap#main`很长,所以我们一段一段来分析其启动时加载策略,以便我们更好理解源码。

```java
public static void main(String args[]) {

        synchronized (daemonLock) {
            if (daemon == null) {
                // Don't set daemon until init() has completed
                Bootstrap bootstrap = new Bootstrap();
                try {
                    bootstrap.init();
                } catch (Throwable t) {
                    handleThrowable(t);
                    t.printStackTrace();
                    return;
                }
                daemon = bootstrap;
            } else {
                // When running as a service the call to stop will be on a new
                // thread so make sure the correct class loader is used to
                // prevent a range of class not found exceptions.
                Thread.currentThread().setContextClassLoader(daemon.catalinaLoader);
            }
        }
	    //其他内容
    	. . . . . .
    }

```

首先要理解一个点,也就是这个`  bootstrap`是做什么的。在很多程序中我们都能够见到这个词,例如Netty，而本身意思带有引导的含义,也就是说,`bootstrap`并不是真正运行我们程序的线程,而是一个在程序开始前进行资源准备以及加载的类。

有了这个基础我们再来看这段代码就比较清晰了。首先是`  daemon`。

```java
private static volatile Bootstrap daemon = null;
```

这是一个可见的变量,由于tomcat可能会开启多个实例,所以我们必须保证自己的daemon是单例的,这里也是一种单例模式的启动。

如果tomcat在启动时发现daemon为空,则会实例化一个`  bootstrap`对象来完成初始化的方法,这也是第一个重点。

### init方法

官方文档上对于`  init`方法的描述为

> Load the Catalina daemon.

下面我们来看下代码中的一些细节。

```java
public void init() throws Exception {

    initClassLoaders();

    Thread.currentThread().setContextClassLoader(catalinaLoader);

    SecurityClassLoad.securityClassLoad(catalinaLoader);

    // Load our startup class and call its process() method
    if (log.isDebugEnabled())
        log.debug("Loading startup class");
    Class<?> startupClass = catalinaLoader.loadClass("org.apache.catalina.startup.Catalina");
    Object startupInstance = startupClass.getConstructor().newInstance();

    // Set the shared extensions class loader
    if (log.isDebugEnabled())
        log.debug("Setting startup class properties");
    String methodName = "setParentClassLoader";
    Class<?> paramTypes[] = new Class[1];
    paramTypes[0] = Class.forName("java.lang.ClassLoader");
    Object paramValues[] = new Object[1];
    paramValues[0] = sharedLoader;
    Method method =
        startupInstance.getClass().getMethod(methodName, paramTypes);
    method.invoke(startupInstance, paramValues);

    catalinaDaemon = startupInstance;
}
```

#### 类加载器初始化过程

首先是第一个方法`  initClassLoaders`,见名知意,是对类加载器的一些操作。

```java
ClassLoader commonLoader = null;
ClassLoader catalinaLoader = null;
ClassLoader sharedLoader = null;
```

在`  bootstrap`有这三个成员变量,分别对应了三个组件的加载器。而`  initClassLoaders`方法则是对这三个加载器的初始化操作。

```java
private void initClassLoaders() {
    try {
        commonLoader = createClassLoader("common", null);
        if (commonLoader == null) {
            // no config file, default to this loader - we might be in a 'single' env.
            commonLoader = this.getClass().getClassLoader();
        }
        catalinaLoader = createClassLoader("server", commonLoader);
        sharedLoader = createClassLoader("shared", commonLoader);
    } catch (Throwable t) {
        handleThrowable(t);
        log.error("Class loader creation threw exception", t);
        System.exit(1);
    }
}
```

内部没有什么太多可以说的,主要还是看一下这个方法中调用次数最多的`  createClassLoader`。

```java
private ClassLoader createClassLoader(String name, ClassLoader parent)
    throws Exception {

    String value = CatalinaProperties.getProperty(name + ".loader");
    if ((value == null) || (value.equals("")))
        return parent;

    value = replace(value);

    List<Repository> repositories = new ArrayList<>();

    String[] repositoryPaths = getPaths(value);

    //JAR的查找过程,这里不是关注的重点
    . . . . . .
    return ClassLoaderFactory.createClassLoader(repositories, parent);
}
```

由于后半段主要是寻找JAR和定位的过程,我们就不详细分析,我们应该重点分析前半部分,也就是类加载过程。

能够注意到,参数中有一个`ClassLoader parent  `，如果你足够敏感,一定能够联想到类加载机制中的双亲委派机制。关于双亲委派机制究竟做了什么,以及如何工作,不是本篇文章重点,如果有兴趣,这里给出一篇文章,可以详细了解。

> 关于Java类加载器双亲委派机制的思考 : https://www.cnblogs.com/lanxuezaipiao/p/4138511.html

所以整个加载过程实际上是逐层委托给上层的类加载器来创建的,当到达顶层的类加载器时,才会将类加载进入JVM。

我们再回到`  initClassLoaders`方法中,需要注意的是只有`  commonLoader`的上层加载器为null。

```java
commonLoader = createClassLoader("common", null);
if (commonLoader == null) {
    // no config file, default to this loader - we might be in a 'single' env.
    commonLoader = this.getClass().getClassLoader();
}
```

也就是说,`  commonLoader`是整个加载器的最顶层,只有当它被创建后,后续的`  shared`,`  catalina`才能够被加载。这也是`  common`组件被广泛运用于tomcat各处的原因所在。

#### catalina实例化

```java
// Load our startup class and call its process() method
if (log.isDebugEnabled())
    log.debug("Loading startup class");
Class<?> startupClass = catalinaLoader.loadClass("org.apache.catalina.startup.Catalina");
Object startupInstance = startupClass.getConstructor().newInstance();
```

之前已经通过双亲委派将三个类加载器创建完成,下面就是通过`  catalinaLoader`创建` catalina ` 实例。这个过程是通过反射方式完成的。

#### 命令行参数设置

前半部分`  init`方法基本解决,下面比较简单,我们来看一下。

```java
public static void main(String args[]) {
	//上半部分
	. . . . . .
    try {
        String command = "start";
        if (args.length > 0) {
            command = args[args.length - 1];
        }

        if (command.equals("startd")) {
            args[args.length - 1] = "start";
            daemon.load(args);
            daemon.start();
        } else if (command.equals("stopd")) {
            args[args.length - 1] = "stop";
            daemon.stop();
        } else if (command.equals("start")) {
            daemon.setAwait(true);
            daemon.load(args);
            daemon.start();
            if (null == daemon.getServer()) {
                System.exit(1);
            }
        } else if (command.equals("stop")) {
            daemon.stopServer(args);
        } else if (command.equals("configtest")) {
            daemon.load(args);
            if (null == daemon.getServer()) {
                System.exit(1);
            }
            System.exit(0);
        } else {
            log.warn("Bootstrap: command \"" + command + "\" does not exist.");
        }
    } catch (Throwable t) {
        // Unwrap the Exception for clearer error reporting
        if (t instanceof InvocationTargetException &&
            t.getCause() != null) {
            t = t.getCause();
        }
        handleThrowable(t);
        t.printStackTrace();
        System.exit(1);
    }
}
```

下半部分的主要是对于启动时命令函参数的一些设置。

> Java中`  main`函数的参数`String[] args`是在通过命令行启动时附带的参数,例如 : 
>
> javac test.java
>
> java test val1 val2 val3
>
> 如果输出args的话会输出后面的三个参数。

tomcat中使用了这种方式来完成对于命令行的处理,可以看到实际上就是设置了一些启动级别,根据参数来在启动的时候完成设置。

至此,bootstrap启动过程已经分析完毕。

## 总结

本文使用大量篇幅加代码来阐述`  bootstrap`启动过程,整个过程就是定位创建catalinaBaseFile和catalinaHomeFile,然后通过双亲委派机制加载三个加载器,最后通过init方法来创建,并且能够获取命令行参数。

整个过程实际上还是比较清晰有逻辑的,在阅读tomcat源码时,一定要先梳理其运行逻辑,按照逻辑逐步抽离,否则会陷入左右为难的过程。

## 参考资料

- [CATALINA_BASE VS CATALINA_HOME](https://stackoverflow.com/questions/3090398/tomcat-catalina-base-and-catalina-home-variables)
- [Tomcat源码分析](https://juejin.im/post/5af176196fb9a07ac90d2ac8)







