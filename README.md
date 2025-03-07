# 基于百度CachedUidGenerator实现的雪花算法: https://github.com/baidu/uid-generator/blob/master/README.zh_cn.md
# 核心部分: 不变
# 改进点:
  1. maven依赖最小化
  2. 核心的接口抽象成单独模块
  3. 配置最小化; 几乎零配置
  4. 机器id的分配方式: 支持配置文件, 随机生成, redis, mysql, postgres等5种方式

# 使用
  1. 依赖
     
     groupId: io.github.xiapxx
     
     artifactId: uid-generator-spring-boot-starter
     
     version: 1.0.1
       
 3. 生成id
    class XXXService {
        @Autowired
        private UidGenerator uidGenerator;

        void XXMethod(){
            long uid = uidGenerator.getUID(); // 生成id
        }
    }
    

# 关于机器id的生成, 可选择如下5种方式:    
 1. 随机生成机器id方式, 无需任何配置, 也是默认方式. 多个应用实例时不保证唯一, 如果是单机应用，可使用这种方式;
 2. 手动指定机器id, 多个应用实例时, 每个应用实例需配置不同的id, 配置如下:
    
    // 应用1
  
    uid.generator.worker.id=1  
  
     // 应用2
   
    uid.generator.worker.id=2

 4. redis方式自动生成机器id, 配置如下:
    
     // 配置spring.redis的相关配置; 
 
     groupId : org.springframework.data
    
     artifactId : spring-data-redis
 
     // 配置 
  
      uid.generator.worker.type=redis
  
6. mysql方式自动生成机器id, 配置如下:
  // 创建如下表:
  create table uid_generator_worker_id  (
    id bigint(20) not null auto_increment,
    primary key (id) using btree
  );
 // 配置 
 uid.generator.worker.type=mysql
 // mysql的驱动类
 uid.generator.worker.dataSource.driverClass=xxxx 
 uid.generator.worker.dataSource.url=jdbc:mysql://xxxxx/xxx
 uid.generator.worker.dataSource.username=xxxx
 uid.generator.worker.dataSource.password=xxx

7. postgres方式自动生成机器id, 配置如下：
  // 创建如下序列 
 create sequence uid_generator_worker_id_seq start 1;
 // 配置 
 uid.generator.worker.type=postgres
 // postgres的驱动类
 uid.generator.worker.dataSource.driverClass=xxxx 
 uid.generator.worker.dataSource.url=jdbc:postgresql://xxxxx/xxx
 uid.generator.worker.dataSource.username=xxxx
 uid.generator.worker.dataSource.password=xxx


 
  

