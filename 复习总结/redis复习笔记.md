## Redis数据结构与基础

在Redis中一共有五种数据结构

- String类型 底层使用Char[]
- Hash类型  字典(使用Hash表作为实现),对于hash冲突使用链寻址法,类似于hashmap
- List类型 双向链表
- set无需集合
- zset有序集合(底层使用跳表数据结构)

常用数据结构

### SDS

|                C字符串                 | SDS                                          |
| :------------------------------------: | -------------------------------------------- |
|       获取字符串长度复杂度为O(N)       | 获取字符串长度复杂度为O(1)                   |
|  API是不安全的，可能会造成缓冲区溢出   | API是安全的，不会造成缓冲区溢出              |
| 修改字符串长度必然会需要执行内存重分配 | 修改字符串长度N次最多会需要执行N次内存重分配 |
|            只能保存文本数据            | 可以保存文本或二进制数据                     |
|         可以使用所有库中的函数         |                                              |

底层依旧使用char[]数组实现，只不过和C中区别不同

当数据在1m以下,首次分配会分配数据本身两倍的容量,当数据量在1m以上,会分配数据量+1m的容量。

3.2版本后，会动态分配数据

```C
static inline char sdsReqType(size_t string_size) {
    if (string_size < 1<<5)  // 32
        return SDS_TYPE_5;
    if (string_size < 1<<8)  // 256
        return SDS_TYPE_8;
    if (string_size < 1<<16)   // 65536 64k
        return SDS_TYPE_16;
    if (string_size < 1ll<<32)  // 4294967296 4G
        return SDS_TYPE_32;
    return SDS_TYPE_64;
}
```

避免在对字符串重新append操作时重新分配内存。

### 链表

C语言中并没有实现链表结构，所以在Redis中自定义了链表

```c
typedef struct listNode {
    // 前置节点
    struct listNode *prev;
    // 后置节点
    struct listNode *next;
    // 节点值
    void *value;
} listNode;
```

```c
typedef struct list {
    // 链表头节点
    listNode *head;
    // 链表尾节点
    listNode *tail;
    // 节点值复制函数
    void *(*dup)(void *ptr);
    // 节点值释放函数
    void (*free)(void *ptr);
    // 节点值对比函数
    int (*match)(void *ptr, void *key);
    // 链表所包含的节点数量
    unsigned long len;
} list;
```

![](https://s2.ax1x.com/2020/03/11/8A75tS.png)

**链表特性**

- 双端链表 : 可以实现队列操作，例如`LPUSH`，`RPOP`，获取头尾节点的时间复杂度为O(1)
- 无环 : 表头`prev`和`next`都指向Null，不会成环
- 长度 : 获取`length`的时间复杂度为O(1)
- 多态 : 链表节点可以保存不同类型

### 字典

结构如下

![](https://s2.ax1x.com/2020/03/11/8E16o9.png)

主要分为以下三个结构

- dict字典 包含多个HashMap，其中含有HashMap索引
- dicht哈希表 包含一个HashMap和关于Map的相关信息，例如Map大小，已经使用的大小
- HashMap和节点 传统的HashMap，使用链地址法来解决Hash冲突

当一个新的键值对要添加到字典中时，会根据键值对的键计算出哈希值和索引值，根据索引值放到对应的哈希表上，即如果索引值为0，则放到`ht[0]`哈希表上。当有两个或多个的键分配到了哈希表数组上的同一个索引时，就发生了**键冲突**的问题，哈希表使用**链地址法**来解决，即使用哈希表节点的`next`指针，将同一个索引上的多个节点连接起来。当哈希表的键值对太多或太少，就需要对哈希表进行扩展和收缩，通过`rehash`(重新散列)来执行

#### rehash过程

字典中ht 中存储了2个哈希表字段,主要作用是用来扩容,当第一次创建字典时,会在`  ht[0]`创建一个hashmap,当数据存储到上限时,需要进行扩容(当其中数据小于存储数量的1/10时会自动缩容),扩容容量为`  2^used`，当决定扩容时,会在`  ht[1]`创建一个新的大小的hashmap,然后将`  ht[0]`哈希表中的数据重新散列进入新的map。

**渐进式rehash**

**这个过程是分批次完成的,而非一次将所有数据全部散列进入新的map。**由于map中可能存在非常多的键值对,如果一次全部rehash,会造成系统停顿,所以会多次进行散列。

### 跳跃表

Redis的跳跃表实现是由`redis.h/zskiplistNode`和`redis.h/zskiplist`（3.2版本之后redis.h改为了server.h）两个结构定义，`zskiplistNode`定义跳跃表的节点，`zskiplist`保存跳跃表节点的相关信息

普通链表查找时间复杂度是O(n)，跳跃表能够达到logn

```c
/* ZSETs use a specialized version of Skiplists */
typedef struct zskiplistNode {
    // 成员对象 （robj *obj;）
    sds ele;
    // 分值
    double score;
    // 后退指针
    struct zskiplistNode *backward;
    // 层
    struct zskiplistLevel {
        // 前进指针
        struct zskiplistNode *forward;
        // 跨度
        // 跨度实际上是用来计算元素排名(rank)的，在查找某个节点的过程中，将沿途访过的所有层的跨度累积起来，得到的结果就是目标节点在跳跃表中的排位
        unsigned long span;
    } level[];
} zskiplistNode;

typedef struct zskiplist {
    // 表头节点和表尾节点
    struct zskiplistNode *header, *tail;
    // 表中节点的数量
    unsigned long length;
    // 表中层数最大的节点的层数
    int level;
} zskiplist;

```

`zskiplistNode`结构

- `level`数组（层）：每次创建一个新的跳表节点都会根据幂次定律计算出level数组的大小，也就是次层的高度，每一层带有两个属性-**前进指针**和**跨度**，前进指针用于访问表尾方向的其他指针；跨度用于记录当前节点与前进指针所指节点的距离（指向的为NULL，阔度为0）
- `backward`（后退指针）：指向当前节点的前一个节点
- `score`（分值）：用来排序，如果分值相同看成员变量在字典序大小排序
- `obj`或`ele`：成员对象是一个指针，指向一个字符串对象，里面保存着一个sds；在跳表中各个节点的成员对象必须唯一，分值可以相同

`zskiplist`结构

- `header`、`tail`表头节点和表尾节点
- `length`表中节点的数量
- `level`表中层数最大的节点的层数

结构 : 

![](https://s2.ax1x.com/2020/03/11/8ksSX9.png)

### 集合

在Redis中,`  set`底层使用的数据结构是`  intset`和`  hashtable`，其中hashtable比较容易理解,key是set中的值,value是null。

整数集合（intset）是Redis用于保存整数值的集合抽象数据结构，可以保存类型为int16_t、int32_t、int64_t的整数值，并且保证集合中不会出现重复元素

整数集合是集合（Set）的底层实现之一，如果一个集合只包含整数值元素，且元素数量不多时，会使用整数集合作为底层实现

```c
typedef struct intset {
    // 编码方式
    uint32_t encoding;
    // 集合包含的元素数量
    uint32_t length;
    // 保存元素的数组
    int8_t contents[];
} intset;
```

- `contents`数组：整数集合的每个元素在数组中按值的大小从小到大排序，且不包含重复项

- `length`记录整数集合的元素数量，即contents数组长度

- `encoding`决定contents数组的真正类型，如INTSET_ENC_INT16、INTSET_ENC_INT32、INTSET_ENC_INT64

![](https://s2.ax1x.com/2020/03/11/8Ef67F.png)

**整数集合升级**

当想要添加一个新元素到整数集合中时，并且新元素的类型比整数集合现有的所有元素的类型都要长(例如在int集合中添加一个long数据,所有数据都要升级为long)，整数集合需要先进行升级（upgrade），才能将新元素添加到整数集合里面。每次想整数集合中添加新元素都有可能会引起升级，每次升级都需要对底层数组已有的所有元素进行类型转换

升级添加新元素：

- 根据新元素类型，扩展整数集合底层数组的空间大小，并为新元素分配空间
- 把数组现有的元素都转换成新元素的类型，并将转换后的元素放到正确的位置，且要保持数组的有序性
- 添加新元素到底层数组

整数集合的升级策略可以提升整数集合的灵活性，并尽可能的节约内存

另外，整数集合不支持降级，一旦升级，编码就会一直保持升级后的状态

### 压缩列表

一个压缩列表可以包含多个节点（entry），每个节点可以保存一个字节数组或者一个整数值

![](https://s2.ax1x.com/2020/03/11/8E44eK.png)

各部分组成说明如下

- `zlbytes`：记录整个压缩列表占用的内存字节数，在压缩列表内存重分配，或者计算`zlend`的位置时使用
- `zltail`：记录压缩列表表尾节点距离压缩列表的起始地址有多少字节，通过该偏移量，可以不用遍历整个压缩列表就可以确定表尾节点的地址
- `zllen`：记录压缩列表包含的节点数量，但该属性值小于UINT16_MAX（65535）时，该值就是压缩列表的节点数量，否则需要遍历整个压缩列表才能计算出真实的节点数量
- `entryX`：压缩列表的节点
- `zlend`：特殊值0xFF（十进制255），用于标记压缩列表的末端

每个压缩列表节点可以保存一个字节数字或者一个整数值，结构如下

![](https://s2.ax1x.com/2020/03/11/8E4IoD.png)

- `previous_entry_ength`：记录压缩列表前一个字节的长度
- `encoding`：节点的encoding保存的是节点的content的内容类型
- `content`：content区域用于保存节点的内容，节点内容类型和长度由encoding决定

### 过期策略删除

Redis可以设置过期时间,至于过期键什么时候删除有三种策略

- 定时删除 : 创建过期键时,同时会创建一个定时器,计算时间,到达过期时间自动删除
- 惰性删除 : 放任键过期不管,只有在下次查询该键时,进行时间比对,如果过期则删除,否则返回
- 定期删除 : 定期对整个Redis进行一次检测,然后删除过期键

而这几种情况可以分为两种情况,CPU吞吐友好和内存友好,定时删除会消耗更多的CPU资源,但是能够保证内存时刻最小化,惰性删除适用于CPU资源紧张,内存比较宽松的情况,定期删除综合二者特点,适用于大部分情况。

**删除策略具体实现**

惰性删除基于一种类似拦截器的操作实现,使用`  expireIfNeeded`函数,每当获取一个键时,会拦截请求并调用该方法检测键是否过期,如果过期则进行删除,否则正常返回。

定时删除则是没过一段时间,都会选取数据库中的一定量的key进行过期检测,并删除其中的过期键。

在保存AOF和RDB文件时,同样会对过期键进行检查，使用RDB时,会在`  save`时不会将过期键进行记录,而在AOF中,不会对过期键做任何处理,在之后操作例如定期删除或者惰性删除等,会在AOF之后追加一条记录

## Redis缓存问题

Redis可以作为缓存服务器来使用,作为缓存需要注意以下几种情况。

### 缓存一致性问题

当数据库数据更新时,可以选择先删除缓存内容,也可以选择后删除缓存内容,但是这两种情况都会带来缓存一致性问题。

- **先删除缓存** : 当一个线程删除缓存,同时准备更新数据库数据时，另一个线程又读取了数据，并把它同步到了缓存，此时读到的数据就是脏数据，因此会导致缓存一致性问题。

- **后删除缓存** : 如果先写了库，在删除缓存前，写库的线程宕机了，没有删除掉缓存，则也会出现数据不一致情况。

**解决方法**

延时双删除策略 : 在写库操作前后都对缓存进行删除操作,二者之间做一定延迟处理

设置缓存过期时间 : 每段时间后缓存自动失效，只要达到过期时间，再有请求自然会读取数据库

异步更新缓存(Mysql + 消息队列 + Redis) : 

1. 读操作都在Redis进行，写操作在Mysql进行
2. 全量读取将所有数据读取到Redis,增量读取(insert,update,delete)实时更行
3. 每当出现增量读取后，将更新内容推送到缓存中更新数

### 缓存雪崩问题

缓存雪崩指缓存数据在某一个时间段内集中过期失效,此时如果有大量的请求就会落到数据库上，造成数据库的周期性压力

**解决方法**

**随机因子** : 每次为缓存过期时间添加一个随机因子`  insertInRedis(overdue+randomTime)`,尽量保证数据不会出现某个时间点全部失效的情况，将请求尽量散列到均匀的时间戳上。

**加锁排队** : 在**并发量不高的情况下**可以使用加锁排队方法，在缓存过期时间内，用户会阻塞，然后重新读取数据加入缓存，其他用户再读取时就能够从Redis中读取。但是如果并发量高，1000个用户会阻塞999个，会出现超时的情况。

**主动更新** : 使用异步更新缓存的方式，失效后主动更新缓存，防止大量请求直接落到数据库中

### 缓存穿透

缓存穿透指请求一个数据库中不存在的数据，则每一次查询都会跳过缓存直接达到数据库，这种查询不存在数据情况被称为缓存穿透，缓存穿透容易被恶意利用，达到短时间内大量请求冲击数据库的目的。

**解决方法**

**缓存空值 : ** 如果查询到为空的数据，将该数据的键保存在缓存中，同时将值标记为null，但是需要设置过期时间，防止期间出现数据库增添该数据的情况造成缓存不一致的情况。

**布隆过滤器** : 布隆过滤器能够保证一个数据不一定存在，但一定不存在的情况。将数据库中所有的Key存放进入布隆过滤器中，每次查询先去布隆过滤器查找，如果没有则直接返回null。

> Bloom Filter : https://juejin.im/post/5de1e37c5188256e8e43adfc

### 缓存击穿

当高并发系统大量请求同一个Key时，该Key失效了，将有大量的请求直接落在数据库上，导致数据库压力增大。

**解决方法**

使用互斥锁，当缓存失效后，同一时间只有一个线程能够请求，请求完成后将数据放回缓存，其他线程此时再去请求缓存中的数据。

## Redis持久化

### 持久化特性

Redis提供了不同级别的持久化方案

- RDB : 指定时间间隔对数据进行快照储存
- AOF : 记录每此对服务器的写操作，AOF命令将redis协议追加到文件末尾，保证文件不会过大

### RDB

RDB是一个紧凑文件，能够保存某个时间点中Redis内的数据快照，适用于灾难恢复。Redis在保存RDB文件时，父线程会fork一个子线程，此时父线程不再进行IO操作，子线程进行RDB文件快照的写入,在恢复数据集的时候，RDB更方便一些。

但是如果出现意外断电等操作，RDB可能会丢失一段时间内的数据集，因为RDB适用于每隔五分钟并且有100个写入的情况这种保存模式，一旦断电丢失可能会导致一段时间内的数据丢失。

同样如果数据集巨大，fork的子线程不能立即将所有数据都储存在RDB文件上，此时父线程需要暂时停止IO，可能导致响应缓慢,停顿等情况。

**工作过程**

- Redis 调用forks. 同时拥有父进程和子进程。
- 子进程将数据集写入到一个临时 RDB 文件中。
- 当子进程完成对新 RDB 文件的写入时，Redis 用新 RDB 文件替换原来的 RDB 文件，并删除旧的 RDB 文件。这种工作方式使得 Redis 可以从写时复制（copy-on-write）机制中获益。



### AOF

AOF提供了三种策略 : 1.不进行fsync  2.每秒进行一次fsync  3.每次写入都进行fsync

一般来说使用每秒fsync处理，通常状况下，每秒同步性能损耗很小，同时如果断电宕机等情况最多丢失一秒的数据。

在AOF数据集过大的时候，Redis会对其进行重写操作以减小体积，重写完成后会使用新的AOF

缺点 : 

AOF文件通常比RDB文件大一些，更具fsync策略，可能会比RDB持久化方式慢一些(体现不明显)

**日志重写**

因为 AOF 的运作方式是不断地将命令追加到文件的末尾， 所以随着写入命令的不断增加， AOF 文件的体积也会变得越来越大。举个例子， 如果你对一个计数器调用了 100 次 INCR ， 那么仅仅是为了保存这个计数器的当前值， AOF 文件就需要使用 100 条记录（entry）。然而在实际上， 只使用一条 SET 命令已经足以保存计数器的当前值了， 其余 99 条记录实际上都是多余的。

为了处理这种情况， Redis 支持一种有趣的特性： 可以在不打断服务客户端的情况下， 对 AOF 文件进行重建（rebuild）。执行 BGREWRITEAOF 命令， Redis 将生成一个新的 AOF 文件， 这个文件包含重建当前数据集所需的最少命令。Redis 2.2 需要自己手动执行 BGREWRITEAOF 命令； Redis 2.4 则可以自动触发 AOF 重写， 具体信息请查看 2.4 的示例配置文件。

**AOF文件损坏解决方法**

当写入AOF文件时间宕机，AOF可能会损坏，可以如下解决

- 为现有的 AOF 文件创建一个备份。

- 使用 Redis 附带的 redis-check-aof 程序，对原来的 AOF 文件进行修复:

  $ redis-check-aof –fix

- （可选）使用 diff -u 对比修复后的 AOF 文件和原始 AOF 文件的备份，查看两个文件之间的不同之处。

- 重启 Redis 服务器，等待服务器载入修复后的 AOF 文件，并进行数据恢复。

## Redis缓存淘汰策略

当缓存到达内存上限的时候，需要淘汰掉缓存中的数据，一般淘汰算法有如下几种

- FIFO : First in Last out 先进先出法，队列模型
- LRU : Least Recently Used 最近时间内用的最少的数据
- LFU : Least Frequently Used 一段时间内使用最不频繁的数据

在Redis中提供了如下几种淘汰策略 : 

- volatile-ttl：从已设置过期时间的数据集（server.db[i].expires）中挑选将要过期的数据淘汰
- volatile-random：从已设置过期时间的数据集（server.db[i].expires）中任意选择数据淘汰

- allkeys-lru：从数据集（server.db[i].dict）中挑选最近最少使用的数据淘汰

- allkeys-random：从数据集（server.db[i].dict）中任意选择数据淘汰

- no-enviction（驱逐）：禁止驱逐数据
- voltile-lru：从已设置过期时间的数据集（server.db[i].expires）中挑选最近最少使用的数据淘汰

## Redis事务

在Redis中，使用`  MULTI`开启事务`  EXEC`退出事务。事务是一个单独的隔离操作，所有命令都会序列化，按照顺序执行，也是一个原子操作。`  DISCARD`可以放弃事务，`  WATCH`可以为事务提供check-and-set(CAS)行为。

### 用法

`  MULTI`开启事务后，总会返回OK，客户端可以发送多条命令，但是不会立即执行，会被放入一个Queue操作队列，当`  EXEC`执行后会一起执行命令,在事务开启后，可以使用`  DISCARD`清空队列并中断事务。

### 事务中的错误

- 事务在执行 `  EXEC`之前，入队的命令可能会出错。比如说，命令可能会产生语法错误（参数数量错误，参数名错误，等等），或者其他更严重的错误，比如内存不足（如果服务器使用 `maxmemory` 设置了最大内存限制的话）。
- 命令可能在`  EXEC`调用之后失败。举个例子，事务中的命令可能处理了错误类型的键，比如将列表命令用在了字符串键上面，诸如此类。

第一种情况服务器会对入队的操作进行记录，在调用`  EXEC`会拒绝并放弃这个事务，第二种情况出错的操作不会被执行，其他操作正常执行。

### 为什么不支持回滚

Redis不支持原因有二

- Redis命令只会因为错误语法失败，这是编程错误不是并发错误，能够主动避免
- 如果回滚，会对Redis内部简单的结构和高效性能有所影响

### WATCH

被` WATCH`的键会被监视，并会发觉这些键是否被改动过了。 如果有至少一个被监视的键在`EXEC  `执行之前被修改了， 那么整个事务都会被取消， `  EXEC`返回`  nil-reply`来表示事务已经失败。

## Redis分区操作

分区的主要目的 : 

- 让Redis能够管理更大的内存区域，可以使用多台机器的内存区域，提高系统性能
- 分区能够使Redis性能通过提升机器的数量得到有效的增加

### 范围分区

![](https://s1.ax1x.com/2020/03/12/8e2SCd.png)

范围分区已经是一种比较成熟的方案，讲储存的数据映射到不同的Redis上。这种情况需要维护一张映射关系表，这张表需要谨慎维护每一个类和Redis的映射关系，这种方案的效率较低。

### 散列分区

散列分区是使用hash法，不再需要object:id这种格式来进行分区。

- 使用散列函数 (如 `crc32` )将键名称转换为一个数字。例：键`foobar`, 使用`crc32(foobar)`函数将产生散列值`93024922`。
- 对转换后的散列值进行取模，以产生一个0到3的数字，以便可以使这个key映射到4个Redis实例当中的一个。`93024922 % 4` 等于 `2`, 所以 `foobar` 会被存储到第2个Redis实例。 **R2** *注意: 对一个数字进行取模，在大多数编程语言中是使用运算符%*

![](https://s1.ax1x.com/2020/03/12/8e5ze1.png)

公式可以总结为 : 

```
index = hash mod(取模) N(节点个数)
```

### 一致性哈希算法

![](https://s1.ax1x.com/2020/03/13/8K9G1e.png)

一致性hash算法广泛用于memcache，Nginx负载均衡和各类RPC框架中。它主要解决传统hash函数在添加hash表槽后需要重新计算hash值的问题。

在缓存中，hash算法过程如下 : 

- 通过键计算得到Hash值，根据hash值获取到应该储存的Key服务器
- 服务器获取数据，保存
- 查询数据时，先Hash Key，定位相同的服务器，获取到数据。

问题这种情况会出现一些漏洞，假如某个节点宕机后，N值发生变化，当再次进行数据查找时，相同的`  KEY`得到的节点会不同，这就意味着会出现大量数据无法获取的情况，必须对旧的键进行rehash操作，这会浪费大量的时间。

一致性hash算法尽可能减少服务器数量改变所导致缓存的变更。

在一致性Hash算法中，每一个Redis节点都会被连接成为一个哈希环，节点值为IP和端口号的Hash值，而数据获取Hash值后，会储存在顺时针最近的节点中，例如图中的K会被储存在B，TM在C，V在D，当B节点挂掉时，只会影响到本节点上的内容，对于其他节点的散列顺序不会产生影响，减少了全部数据Rehash的操作消耗。

![](https://s1.ax1x.com/2020/03/12/8mnVPI.png)

**MurmurHash 算法**：高运算性能，低碰撞率，由 Austin Appleby 创建于 2008 年，现已应用到 Hadoop、libstdc++、nginx、libmemcached 等开源系统。Java 界中 Redis，Memcached，Cassandra，HBase，Lucene和Guava 都在使用它。

### 虚拟槽分区

**虚拟槽分区** 巧妙地使用了 **哈希空间**，使用 **分散度良好** 的 **哈希函数** 把所有数据 **映射** 到一个 **固定范围** 的 **整数集合** 中，整数定义为 **槽**（`slot`）。这个范围一般 **远远大于** 节点数，比如 `Redis Cluster` 槽范围是 `0 ~ 16383`。**槽** 是集群内 **数据管理** 和 **迁移** 的 **基本单位**。采用 **大范围槽** 的主要目的是为了方便 **数据拆分** 和 **集群扩展**。每个节点会负责 **一定数量的槽**，如图所示：

![](https://s1.ax1x.com/2020/03/13/8K96Xj.png)

当前集群有 `5` 个节点，每个节点平均大约负责 `3276` 个 **槽**。由于采用 **高质量** 的 **哈希算法**，每个槽所映射的数据通常比较 **均匀**，将数据平均划分到 `5` 个节点进行 **数据分区**。`Redis Cluster` 就是采用 **虚拟槽分区**。

- **节点1**： 包含 `0` 到 `3276` 号哈希槽。
- **节点2**：包含 `3277`  到 `6553` 号哈希槽。
- **节点3**：包含 `6554` 到 `9830` 号哈希槽。
- **节点4**：包含 `9831` 到 `13107` 号哈希槽。
- **节点5**：包含 `13108` 到 `16383` 号哈希槽。

这种结构很容易 **添加** 或者 **删除** 节点。如果 **增加** 一个节点 `6`，就需要从节点 `1 ~ 5` 获得部分 **槽** 分配到节点 `6` 上。如果想 **移除** 节点 `1`，需要将节点 `1` 中的 **槽** 移到节点 `2 ~ 5` 上，然后将 **没有任何槽** 的节点 `1` 从集群中 **移除** 即可。

> 由于从一个节点将 **哈希槽** 移动到另一个节点并不会 **停止服务**，所以无论 **添加删除** 或者 **改变** 某个节点的 **哈希槽的数量** 都不会造成 **集群不可用** 的状态.

**虚拟槽分区特点**

- 解耦 **数据** 和 **节点** 之间的关系，简化了节点 **扩容** 和 **收缩** 难度。
- **节点自身** 维护槽的 **映射关系**，不需要 **客户端** 或者 **代理服务** 维护 **槽分区元数据**。
- 支持 **节点**、**槽**、**键** 之间的 **映射查询**，用于 **数据路由**、**在线伸缩** 等场景。

### 分区实现

- **客户端分区**就是在客户端就已经决定数据会被存储到哪个redis节点或者从哪个redis节点读取。大多数客户端已经实现了客户端分区。
- **代理分区** 意味着客户端将请求发送给代理，然后代理决定去哪个节点写数据或者读数据。代理根据分区规则决定请求哪些Redis实例，然后根据Redis的响应结果返回给客户端。redis和memcached的一种代理实现就是[Twemproxy](https://github.com/twitter/twemproxy)
- **查询路由(Query routing)** 的意思是客户端随机地请求任意一个redis实例，然后由Redis将请求转发给正确的Redis节点。Redis Cluster实现了一种混合形式的查询路由，但并不是直接将请求从一个redis节点转发到另一个redis节点，而是在客户端的帮助下直接*redirected*到正确的redis节点。

### 分区缺点

1. 设计多个Key的操作将无法执行，例如求交集，因为不同的Key可能会分布在不同的机器上
2. 无法使用多个key的事务操作
3. 分区会让数据处理变得复杂，当需要备份时需要从不同的机器收集RDB/AOF文件
4. 分区时动态扩容会变得困难，集群会对增加和删除节点进行再平衡，但有些客户端并不支持

## Redis I/O多路复用

> 经典面试题目 : Redis为什么快?
>
> 1. Redis在内存中运行，CPU不再是性能瓶颈
> 2. Redis使用I/O多路复用机制，提高效率
> 3. Redis采用单线程模型，操作简单
> 4. 采用高效简洁的数据结构

### 多路IO复用模型

在 I/O 多路复用模型中，最重要的函数调用就是 `select`，该方法的能够同时监控多个文件描述符的可读可写情况，当其中的某些文件描述符可读或者可写时，`select` 方法就会返回可读以及可写的文件描述符个数。

![](https://s1.ax1x.com/2020/03/12/8mNd9U.png)

### Reactor

Redis服务采用Reactor的方式来实现文件事件处理器，每一个连接都对应一个文件描述符

![](https://s1.ax1x.com/2020/03/12/8mdntx.png)

文件事件处理器使用 I/O 多路复用模块同时监听多个 FD，当 `accept`、`read`、`write` 和 `close` 文件事件产生时，文件事件处理器就会回调 FD 绑定的事件处理器。

虽然整个文件事件处理器是在单线程上运行的，但是通过 I/O 多路复用模块的引入，实现了同时对多个 FD 读写的监控，提高了网络通信模型的性能，同时也可以保证整个 Redis 服务实现的简单。

I/O 多路复用模块封装了底层的 `select`、`epoll`、`avport` 以及 `kqueue` 这些 I/O 多路复用函数，为上层提供了相同的接口。

## Redis主从模式

通过` slaveof host port`可以将一个redis服务器配置为一个master服务器的从服务器，从服务器只能进行读取操作，一个主服务器能够配置多个从服务器，从服务器同样能配置slave。

![](https://s1.ax1x.com/2020/03/13/8n1NjI.png)

### 主从复制优点

- 数据冗余，能保证数据热备份
- 能够防止master宕机导致服务器不可用
- 读写分离，master主要负责写入，slave负责读取，提高性能
- 提高可用性，是哨兵模式和集群模式的基础

### 配置

- 5.0版本之前，使用`  slaveof <host> <port>`来配置
- 5.0版本之后，使用`  replicaof <host> <port>`来配置

### 主从复制实现过程

**建立连接**

该阶段发生在服务器发出`  slaveof`命令后，slave会向master发送一个`  ping`命令用于检测连接，如果返回`  pong`则代表master正常，否则slave会自动断开重连。

如果master配置了`  requirepass`则需要在slave配置中配置`  masterauth`，身份验证完成之后，slave会发送自己端口，主服务器会记录下来。

**数据同步**

数据同步是比较重要的部分，slave会向master发送`  PSYNC`命令请求数据同步。

Redis分为部分重同步和完整重同步。

- 部分重同步 : 部分重同步是用于处理断线后重复制的情况
- 完整重同步 : 在第一次建立连接和断线重连时，slave和master会进行完整重同步

**完整重同步**

![](https://s1.ax1x.com/2020/03/13/8nGKun.png)

- 从服务器连接主服务器，发送SYNC命令
- 主服务器接收到SYNC命名后，开始执行`bgsave`命令生成RDB文件并使用缓冲区记录此后执行的所有写命令
- 主服务器`basave`执行完后，向所有从服务器发送快照文件，并在发送期间继续记录被执行的写命令
- 从服务器收到快照文件后丢弃所有旧数据，载入收到的快照
- 主服务器快照发送完毕后开始向从服务器发送缓冲区中的写命令
- 从服务器完成对快照的载入，开始接收命令请求，并执行来自主服务器缓冲区的写命令

**部分重同步**

部分重同步是用于处理断线后重复制的情况，先介绍几个用于部分重同步的部分

- `runid`(replication ID)，主服务器运行id，Redis实例在启动时，随机生成一个长度40的唯一字符串来标识当前节点
- `offset`，复制偏移量。主服务器和从服务器各自维护一个复制偏移量，记录传输的字节数。当主节点向从节点发送N个字节数据时，主节点的offset增加N，从节点收到主节点传来的N个字节数据时，从节点的offset增加N
- `replication backlog buffer`，复制积压缓冲区。是一个固定长度的FIFO队列，大小由配置参数`repl-backlog-size`指定，默认大小1MB。需要注意的是该缓冲区由master维护并且有且只有一个，所有slave共享此缓冲区，其作用在于备份最近主库发送给从库的数据

当slave连接到master，会执行`PSYNC  `发送记录旧的master的`runid`（replication ID）和偏移量`offset`，这样master能够只发送slave所缺的增量部分。但是如果master的复制积压缓存区没有足够的命令记录，或者slave传的`runid`(replication ID)不对，就会进行**完整重同步**，即slave会获得一个完整的数据集副本

![](https://s1.ax1x.com/2020/03/13/8nGRKA.png)

**PSYNC**

![](https://s1.ax1x.com/2020/03/13/8nYPFf.png)

**命令传播**

当完成了完整重同步或者部分重同步后，master和slave数据暂时保持了一致，之后master接收到的每一条命令都会传播给slave进行执行。

在命令传播阶段，slave会每秒发送一次心跳包，进行心跳检测。

-  检测slave和master之间的连接状态
-  检测命令丢失

### 缓冲队列

如果判断从服务器是进行完整重同步还是部分重同步? 在`  master`中会维护一个固定大小的缓冲区,其中,实际上是一个FIFO队列,默认大小为1M。

在队列中,近期存储的数据和偏移量,例如

```
offset 10   11  12  13  14 15 16 17
data   'h' 'e' 'l' 'l' 'o'
```

当slave断线后,会发送自己的偏移量,如果偏移量还在队列中,则进行部分重同步,如果时间较长,缓冲区已经没有slave的偏移量,则会进行完整重同步。

### 持久化不安全性

主从复制中要保证持久化，否则可能出现以下情况。

1. A节点设置为master节点并关闭了持久化功能，节点B,C从A中复制数据
2. A在某一时间崩溃，自动重启系统完成了重启。
3. A中没有任何数据，B,C对其进行复制，由于A是空的，B,C会销毁掉自身所有数据

## Redis高可用

在 `Web` 服务器中，**高可用** 是指服务器可以 **正常访问** 的时间，衡量的标准是在 **多长时间** 内可以提供正常服务（`99.9%`、`99.99%`、`99.999%` 等等）。在 `Redis` 层面，**高可用** 的含义要宽泛一些，除了保证提供 **正常服务**（如 **主从分离**、**快速容灾技术** 等），还需要考虑 **数据容量扩展**、**数据安全** 等等。

在 `Redis` 中，实现 **高可用** 的技术主要包括 **持久化**、**复制**、**哨兵** 和 **集群**，下面简单说明它们的作用，以及解决了什么样的问题：

- **持久化**：持久化是 **最简单的** 高可用方法。它的主要作用是 **数据备份**，即将数据存储在 **硬盘**，保证数据不会因进程退出而丢失。
- **复制**：复制是高可用 `Redis` 的基础，**哨兵** 和 **集群** 都是在 **复制基础** 上实现高可用的。复制主要实现了数据的多机备份以及对于读操作的负载均衡和简单的故障恢复。缺陷是故障恢复无法自动化、写操作无法负载均衡、存储能力受到单机的限制。
- **哨兵**：在复制的基础上，哨兵实现了 **自动化** 的 **故障恢复**。缺陷是 **写操作** 无法 **负载均衡**，**存储能力** 受到 **单机** 的限制。
- **集群**：通过集群，`Redis` 解决了 **写操作** 无法 **负载均衡** 以及 **存储能力** 受到 **单机限制** 的问题，实现了较为 **完善** 的 **高可用方案**。

## Redis Sentinel哨兵模式

### 主从模式问题

- 虽然主从模式在主节点崩溃时可以使用一个从节点来顶替主节点，但是全程需要手动设置，配置Host，Port等内容将变得十分繁琐，效率低下
- 主从模式只是降低了主节点读操作压力，但是写操作，储存空间都要受到单机限制

针对这两种情况，第一种可以使用哨兵模式来解决，第二种可以使用集群模式来解决。

### 哨兵模式模型

Redis Sentinel主要功能包括**主节点存活监控**，**主从运行情况监控**，**自动故障转移**，**主从切换**。

![](https://s1.ax1x.com/2020/03/13/8uZxFs.png)

在Redis Sentinel系统可以管理多个Redis服务器。

- 监控 `  Sentinel`会不断检查主服务器和从服务器的运行状况
- 通知 当被监控的某个`  Redis`服务器出现问题，`  Sentinel`通过API向管理员或者应用程序发送通知
- 自动故障转移 当主节点失效时，`  Sentinel`会通过选举将一个从节点变为主节点，并将其他从节点指向新的主节点
- 配置提供者 在`  Redis Sentinel`模式下，客户端应用初始化时连接的时`  Sentinel`集合中的节点，从中获取主节点信息，类似于一种代理模式。

### 主观下线与客观下线

由于在`  Redis Sentinel`情况下一个重点的问题在于如何判断主节点是否真的下线，由于在`  Sentinel`集合中每个集合点都监控着一个节点，如果我们仅仅通过监控节点来判断主节点的状态不是很准确，例如 : 

- `  Sentinel`向主节点发送了一个ping，然后由于延迟或者阻塞原因，这个ping没有在规定时间内到达
- `  Redis Sentinel`就认为主节点已经下线
- 在主节点存活的情况下强制选举了另一个节点成为主节点，这样将浪费很多资源

所以需要通过主观下线和客观下线来判断。

**主观下线** 适用于所有 **主节点** 和 **从节点**。如果在 `down-after-milliseconds` 毫秒内，`Sentinel` 没有收到 **目标节点** 的有效回复，则会判定 **该节点** 为 **主观下线**。

**客观下线** 只适用于 **主节点**。如果 **主节点** 出现故障，`Sentinel` 节点会通过 `sentinel is-master-down-by-addr` 命令，向其它 `Sentinel` 节点询问对该节点的 **状态判断**。如果超过 `` 个数的节点判定 **主节点** 不可达，则该 `Sentinel` 节点会判断 **主节点** 为 **客观下线**。

总的来说，主管下线在于`  Sentinel`节点基于ping响应的主观判断，客观下线基于其他节点对于主节点的交互情况的判断，当二者都满足时，`  Redis Sentinel`将认为这个节点真的下线。

### Sentinel工作原理

在`  Redis Sentinel`中，`  Sentinel`定期会执行如下任务

- 每个 `Sentinel` 以 **每秒钟** 一次的频率，向它所知的 **主服务器**、**从服务器** 以及其他 `Sentinel`**实例** 发送一个 `PING` 命令。

![](https://s1.ax1x.com/2020/03/13/8uXP2R.png)

- 如果一个 **实例**（`instance`）距离 **最后一次** 有效回复 `PING` 命令的时间超过 `down-after-milliseconds` 所指定的值，那么这个实例会被 `Sentinel` 标记为 **主观下线**。

![](https://s1.ax1x.com/2020/03/13/8ujiFg.png)

- 如果一个 **主服务器** 被标记为 **主观下线**，并且有 **足够数量** 的 `Sentinel`（至少要达到 **配置文件** 指定的数量）在指定的 **时间范围** 内同意这一判断，那么这个 **主服务器** 被标记为 **客观下线**。
- 在一般情况下， 每个 `Sentinel` 会以每 `10` 秒一次的频率，向它已知的所有 **主服务器** 和 **从服务器** 发送 `INFO` 命令。当一个 **主服务器** 被 `Sentinel` 标记为 **客观下线** 时，`Sentinel` 向 **下线主服务器** 的所有 **从服务器** 发送 `INFO` 命令的频率，会从 `10` 秒一次改为 **每秒一次**。

![](https://s1.ax1x.com/2020/03/13/8ujwkD.png)

- Sentinel` 和其他 `Sentinel` 协商 **主节点** 的状态，如果 **主节点** 处于 `SDOWN` 状态，则投票自动选出新的 **主节点**。将剩余的 **从节点** 指向 **新的主节点** 进行 **数据复制**。

![](https://s1.ax1x.com/2020/03/13/8ujIpj.png)

- 当没有足够数量的 `Sentinel` 同意 **主服务器** 下线时， **主服务器** 的 **客观下线状态** 就会被移除。当 **主服务器** 重新向 `Sentinel` 的 `PING` 命令返回 **有效回复** 时，**主服务器** 的 **主观下线状态** 就会被移除。

## Redis集群(转自零壹技术栈)

`  Redis Cluster`集群具有高可用，拓展性，分布式和容错等特性，一般分布式方案有两类

### 客户端分区方案

**客户端** 就已经决定数据会被 **存储** 到哪个 `redis` 节点或者从哪个 `redis` 节点 **读取数据**。其主要思想是采用 **哈希算法** 将 `Redis` 数据的 `key` 进行散列，通过 `hash` 函数，特定的 `key`会 **映射** 到特定的 `Redis` 节点上。

![](https://s1.ax1x.com/2020/03/13/8KpZZt.png)

**客户端分区方案** 的代表为 `Redis Sharding`，`Redis Sharding` 是 `Redis Cluster` 出来之前，业界普遍使用的 `Redis` **多实例集群** 方法。`Java` 的 `Redis` 客户端驱动库 `Jedis`，支持 `Redis Sharding` 功能，即 `ShardedJedis` 以及 **结合缓存池** 的 `ShardedJedisPool`。

- **优点**

不使用 **第三方中间件**，**分区逻辑** 可控，**配置** 简单，节点之间无关联，容易 **线性扩展**，灵活性强。

- **缺点**

**客户端** 无法 **动态增删** 服务节点，客户端需要自行维护 **分发逻辑**，客户端之间 **无连接共享**，会造成 **连接浪费**。

### 分区代理方案

**客户端** 发送请求到一个 **代理组件**，**代理** 解析 **客户端** 的数据，并将请求转发至正确的节点，最后将结果回复给客户端。

- **优点**：简化 **客户端** 的分布式逻辑，**客户端** 透明接入，切换成本低，代理的 **转发** 和 **存储** 分离。
- **缺点**：多了一层 **代理层**，加重了 **架构部署复杂度** 和 **性能损耗**。

![](https://s1.ax1x.com/2020/03/13/8Kp1Mj.png)

**代理分区** 主流实现的有方案有 `Twemproxy` 和 `Codis`。

### 路由查询方案

**客户端随机地** 请求任意一个 `Redis` 实例，然后由 `Redis` 将请求 **转发** 给 **正确** 的 `Redis` 节点。`Redis Cluster` 实现了一种 **混合形式** 的 **查询路由**，但并不是 **直接** 将请求从一个 `Redis` 节点 **转发** 到另一个 `Redis` 节点，而是在 **客户端** 的帮助下直接 **重定向**（ `redirected`）到正确的 `Redis` 节点。

![](https://s1.ax1x.com/2020/03/13/8KpBQJ.png)

- **优点**

**无中心节点**，数据按照 **槽** 存储分布在多个 `Redis` 实例上，可以平滑的进行节点 **扩容/缩容**，支持 **高可用** 和 **自动故障转移**，运维成本低。

- **缺点**

严重依赖 `Redis-trib` 工具，缺乏 **监控管理**，需要依赖 `Smart Client` (**维护连接**，**缓存路由表**，`MultiOp` 和 `Pipeline` 支持)。`Failover` 节点的 **检测过慢**，不如 **中心节点** `ZooKeeper` 及时。`Gossip` 消息具有一定开销。无法根据统计区分 **冷热数据**。

## Redis实现分布式锁

### SETNX命令

基于Redis分布式锁的基础在于`  setnx`命令，该命令操作为`  setnx key value`

- 当key不存在时，value会被设置成功
- 当key存在时，该条指令不会产生任何效果

基于这个命令，我们能够每次让一个线程去`  setnx`一个key，只有成功，才能执行操作，操作完成后删除这个key，这就完成了最简单的redis分布式锁，但是在这个过程中有许多细节需要进行处理。

### Simple Code

下面先来写一个在分布式中线程不安全的程序(Synchronized只能在单机模式下保证并发安全,因为锁基于JVM)，这里使用redis模拟数据库，实际上没有区别，我们从Redis中读取一个库存，然后减一再将新值存入Redis。

这个模型一定是线程不安全的，无论在单机还是分布式系统上，下面我们对程序进行改造。

```java
@RestController
public class RedisController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/getStock")
    public String getStock() {
        String keyName = "stock";
        //从Redis中获取库存
        int stock = Integer.valueOf(stringRedisTemplate.opsForValue().get(keyName));
        if (stock <= 0) {
            return "error : 库存为零,订购失败";
        }
        stock = stock - 1;
        //更改数据库,将数据库库存减1
        stringRedisTemplate.opsForValue().set(keyName, stock + "");
        System.out.println("订购成功,现有库存:" + stock + "件");
        return "success";
    }
}
```

### 第一步 : 加一把锁

首先我们来为程序加一把锁，添加` lockKeyName`字段作为锁的名字,然后尝试操作,如果成功则执行，否则代表已经有其他线程设置了这把锁,当前线程不能执行，直接返回,而持有锁的线程在执行完成之后需要删除锁。

```java
@RequestMapping("/getStock")
    public String getStock() {
        String DBStock = "stock";
        String lockKeyName = "lock";
        //对锁尝试setnx，如果成功，则执行，否则直接返回
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKeyName, "lockVal");
        if (!result) {
            return "程序繁忙,请重新操作";
        }
        //从Redis中获取库存
        int stock = Integer.valueOf(stringRedisTemplate.opsForValue().get(DBStock));
        if (stock <= 0) {
            return "error : 库存为零,订购失败";
        }
        stock = stock - 1;
        //更改数据库,将数据库库存减1
        stringRedisTemplate.opsForValue().set(DBStock, stock + "");
        System.out.println("订购成功,现有库存:" + stock + "件");
        //程序结束后删除锁
        stringRedisTemplate.delete(lockKeyName);
        return "success";
    }
```

### 第二步 : 异常处理

上面这个程序有些问题，假如在获取锁和删除锁之间出现了异常，这把锁将永远不会被释放，所以我们需要将释放锁的步骤放入finally中，就像Lock一样。但是依旧存在一些问题，虽然这样避免了异常的情况，但如果在try的过程中，服务器宕机，程序崩溃掉会如何呢?

答案是锁依旧没有释放，这样就造成其他线程再也无法访问这部分代码块，所以我们需要为每个锁设置一个过期时间，即使锁没有被释放，依旧能够过一段时间后自动释放。

```java
@RequestMapping("/getStock")
    public String getStock() {
        String DBStock = "stock";
        String lockKeyName = "lock";
        //对锁尝试setnx，如果成功，则执行，否则直接返回
        Boolean result = stringRedisTemplate.opsForValue().
                setIfAbsent(lockKeyName, "lockVal",10, TimeUnit.SECONDS);
        try {
            if (!result) {
                return "程序繁忙,请重新操作";
            }
            //从Redis中获取库存
            int stock = Integer.valueOf(stringRedisTemplate.opsForValue().get(DBStock));
            if (stock <= 0) {
                return "error : 库存为零,订购失败";
            }
            stock = stock - 1;
            //更改数据库,将数据库库存减1
            stringRedisTemplate.opsForValue().set(DBStock, stock + "");
            System.out.println("订购成功,现有库存:" + stock + "件");
        } finally {
            //程序结束后删除锁
            stringRedisTemplate.delete(lockKeyName);
        }
        return "success";
    }
```

### 第三步 : 释放自己的锁

在一般情况下，这种分布式锁已经能够满足大部分需求，但是还有一些细节上的问题等待我们完善。

![](https://s1.ax1x.com/2020/03/13/8K5Bon.png)

这里用图来解释比较方便，由于在`  try`中代码块执行事件不一定相同，如果两个线程的执行时间上符合一些特征，就容易出现并发问题。假设A线程执行整个过程需要15S，B线程需要10S，那么会产生如下情况。

- A获取到锁开始执行，此时A具有锁的持有权
- 执行10s后，超过了Key的过期时间，锁被自动删除
- 此时B获取到了锁开始执行
- A执行完成，B执行了一半任务
- A执行`finally`中的释放锁操作，将B持有的锁释放

这种情况在极高的情况下可能带来连锁反应，A释放B，B释放C，会严重造成业务混乱，针对这种情况,最好的办法就是判断这个锁是否是自己持有，线程只能删除自己持有的锁。我们来改造下代码。

```java
@RequestMapping("/getStock")
    public String getStock() {
        String DBStock = "stock";
        String lockKeyName = "lock";
        //生成线程独有的锁UUID值
        String threadLockName = UUID.randomUUID().toString();
        //对锁尝试setnx，如果成功，则执行，否则直接返回
        Boolean result = stringRedisTemplate.opsForValue().
                setIfAbsent(lockKeyName, threadLockName,10, TimeUnit.SECONDS);
        try {
            if (!result) {
                return "程序繁忙,请重新操作";
            }
            //从Redis中获取库存
            int stock = Integer.valueOf(stringRedisTemplate.opsForValue().get(DBStock));
            if (stock <= 0) {
                return "error : 库存为零,订购失败";
            }
            stock = stock - 1;
            //更改数据库,将数据库库存减1
            stringRedisTemplate.opsForValue().set(DBStock, stock + "");
            System.out.println("订购成功,现有库存:" + stock + "件");
        } finally {
            //判断锁是否为自己加的锁
            String val = stringRedisTemplate.opsForValue().get(lockKeyName);
            if (val.equals(threadLockName)) {
                //程序结束后删除锁
                stringRedisTemplate.delete(lockKeyName);
            }
        }
        return "success";
    }
```

### 第四步 : 锁续期

虽然我们使用这种UUID的方式处理了相互解锁的问题，但是我们希望一段代码执行过程中，如果超过了锁的过期时间，能够给锁"续期"，我们可以用一个后台线程来实现这个操作。

```java
new Thread(() -> {
                //如果锁目前还被当前线程持有
                while (stringRedisTemplate.opsForValue().get(lockKeyName).equals(lockKeyName)) {
                    //删除原来的锁
                    stringRedisTemplate.delete(lockKeyName);
                    //重置时间
                    stringRedisTemplate.opsForValue().setIfAbsent(lockKeyName, threadLockName,10, TimeUnit.SECONDS);
                    //线程休眠
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
```

### 第五步 : 原子性保证

虽然看上去分布式锁已经完善，但是由于原子性的问题，这个分布式锁还不是安全的，毕竟我们无法保证每条命令都能以原子性运行，只要操作被割裂，极有可能带来死锁的问题。

在Redisson中，底层使用了Lua脚本语言来对Redis进行操作。Redis在执行Lua语言时，会将其按照原子操作执行，也就是下列内容要么成功要么失败，这样就保证整个过程的完整有序性。

```
if (redis.call('exists', KEYS[1]) == 0) 
then redis.call('hset', KEYS[1], ARGV[2], 1); 
redis.call('pexpire', KEYS[1], ARGV[1]); 
return nil; end; 
if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then redis.call('hincrby', KEYS[1], ARGV[2], 1); redis.call('pexpire', KEYS[1], ARGV[1]); 
return nil; end; 
return redis.call('pttl', KEYS[1]);", Collections.singletonList(this.getName()), new Object[]{this.internalLockLeaseTime, this.getLockName(threadId)});

```

### 不足

上述分布式锁最大的缺点在于，假如有两个线程A,B，A先获取到了`  master`节点1的锁，此时节点1宕机，哨兵模式将`  slave`节点2选为新的`  master`节点，此时线程B去节点2获取到了锁，此时A,B两线程都会认为自己获取到了锁，因此可能在数据库中出现脏数据。

> redis master-slave架构的**主从异步复制**导致的redis分布式锁的最大缺陷：在redis master实例宕机的时候，可能导致多个客户端同时完成加锁。

## 参考资料

- [《Redis设计和实现》](http://redisbook.com/)
- Redis官方文档中心](http://www.redis.cn/documentation.html)
- [Redis哨兵模式与高可用集群](https://juejin.im/post/5b7d226a6fb9a01a1e01ff64)
- [Redis主从复制原理](https://juejin.im/post/5d80ac83e51d45620821cf87)
- [Redis高可用集群原理与搭建](https://juejin.im/post/5b8fc5536fb9a05d2d01fb11)
- [Redis分布式锁原理以及如何续期](https://blog.csdn.net/lzhcoder/article/details/88387751)

