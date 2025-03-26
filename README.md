# uid-generator-spring-boot-starter

## 基于百度CachedUidGenerator实现的雪花算法
[百度CachedUidGenerator](https://github.com/baidu/uid-generator/blob/master/README.zh_cn.md)

## 改进点
1. maven依赖最小化
2. 核心的接口抽象成单独模块
3. 配置最小化; 几乎零配置
4. 机器id的分配方式: 支持随机, 手动指定, redis, mysql, postgres等5种方式

## 如何使用?
### 引入依赖
~~~~xml
<dependency>
    <groupId>io.github.xiapxx</groupId>
    <artifactId>uid-generator-spring-boot-starter</artifactId>
    <version>1.0.1</version>
</dependency>
~~~~
### 使用UidGenerator
    @Autowired
    private UidGenerator uidGenerator;
    ...
        long uid = uidGenerator.getUID()
    ...

## 机器id分配方式
共5种方式: 随机, 手动指定, redis, mysql, postgres。 只需使用其中一种即可

### 随机方式
随机生成机器id, 无需任何配置, 也是默认方式。 但多个应用实例时不保证唯一, 如果是单机应用，可使用这种方式。

### 手动指定方式
多个应用实例时, 每个应用实例需配置不同的id:

    // 应用1
    uid.generator.worker.id=1
    // 应用2
    uid.generator.worker.id=2
    ...

### redis方式
多个应用实例时保证唯一
#### 额外依赖
如果项目中已经使用redis, 推荐这种方式, 因为改动最少
~~~~xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-redis</artifactId>
    <version>2.3.2.RELEASE</version>
</dependency>
~~~~
#### 添加配置
    uid.generator.worker.type=redis

### mysql方式
多个应用实例时保证唯一
#### 创建表
    create table uid_generator_worker_id  (
        id bigint(20) not null auto_increment,
        primary key (id) using btree
    );

#### 添加配置
    uid.generator.worker.type=mysql
    uid.generator.worker.dataSource.driverClass=xxxx // 1.0.2版本可以不指定, 使用SPI机制获取
    uid.generator.worker.dataSource.url=jdbc:mysql://xxxxx/xxx
    uid.generator.worker.dataSource.username=xxxx
    uid.generator.worker.dataSource.password=xxx

### postgres方式
#### 创建序列
    create sequence uid_generator_worker_id_seq start 1;
#### 添加配置
    uid.generator.worker.type=postgres
    uid.generator.worker.dataSource.driverClass=xxxx // 1.0.2版本可以不指定, 使用SPI机制获取
    uid.generator.worker.dataSource.url=jdbc:postgresql://xxxxx/xxx
    uid.generator.worker.dataSource.username=xxxx
    uid.generator.worker.dataSource.password=xxx

## 其他配置
    uid.generator.epochStr=2025-03-07 // 时间基点; 初次上线时指定; 默认值: 2025-03-07
    uid.generator.boostPower=3 //  RingBuffer size扩容参数, 可提高UID生成的吞吐量; 原bufferSize=8192, 扩容后bufferSize= 8192 << 3 = 65536; 默认值: 3
    uid.generator.paddingFactor=50 // 指定何时向RingBuffer中填充UID, 取值为百分比(0, 100), 默认值: 50
    uid.generator.scheduleInterval // 另外一种RingBuffer填充时机, 在Schedule线程中, 周期性检查填充; 默认:不配置此项, 即不实用Schedule线程. 如需使用, 请指定Schedule线程时间间隔, 单位:秒