# tutorials4j

Java 教程

## 项目列表

| 组件            | 作用              |
|---------------|-----------------|
| `framework`   | 基于Spring Boot的框架 |
| `assembly`    | 基于`framework`框架的集成模块 |
| `java21`      | Java21示例项目      |
| `springboot3` | Spring Boot3的示例项目 |
| `springboot4` | Spring Boot4的示例项目 |
| `springcloud` | Spring Cloud的示例项目 |


## 公众号

如果对我的项目代码感兴趣，请关注我的公众号，有很多文章等着你


![杨运交](qrcode_for_gh_31209a11b93e_258.jpg)


## 框架示例图

### 通用模块示例（framework/framework-examples/examples-common）

#### profile:exception

> 统一异常处理
![统一异常处理](docs/images/056.jpg )

> 异步任务执行
![异步任务执行](docs/images/057.jpg )

> UID 生成器
![UID 生成器](docs/images/058.jpg )

### 功能模块示例（framework/framework-examples/examples-feature）

#### profile:signin

> 签到示例
![签到示例](docs/images/001.jpg )

> 签到记录查询示例
![签到记录查询示例](docs/images/023.jpg)



#### profile:schedule

> 任务调度管理
![任务调度管理](docs/images/041.jpg)

> 任务管理
![任务管理](docs/images/042.jpg)

> 任务日志
![任务日志](docs/images/043.jpg)

> prometheus监控日志
![prometheus监控日志](docs/images/047.jpg)

### 缓存模块示例（framework/framework-examples/examples-cache）

#### profile:cacheable

> @Cacheable 示例
![@Cacheable 示例](docs/images/002.jpg)

#### profile:lock

> Redisson 锁示例
![Redisson 锁示例](docs/images/003.jpg)

> Redis 锁示例
![Redis 锁示例](docs/images/004.jpg)

> 本地（JVM） 锁
![本地（JVM） 锁](docs/images/005.jpg)

#### profile:template

> 缓存模版
![本地（JVM） 锁](docs/images/006.jpg)

#### profile:multi-level

> 本地（JVM） 锁
![本地（JVM） 锁](docs/images/007.jpg)

### 验证码模块示例（framework/framework-examples/examples-captcha）

#### profile:simple

> 验证码数据
![验证码数据](docs/images/038.jpg)


#### profile:unified

> hutool验证码接口
![hutool验证码接口](docs/images/008.jpg)
![hutool验证码接口](docs/images/009.jpg)
![hutool验证码接口](docs/images/010.jpg)
![hutool验证码接口](docs/images/011.jpg)

> tianai验证码接口
![tianai验证码接口](docs/images/012.jpg)
![tianai验证码接口](docs/images/013.jpg)
![tianai验证码接口](docs/images/014.jpg)


#### profile:tianai

> tianai验证码官方标准接口
![hutool验证码接口](docs/images/015.jpg)
![hutool验证码接口](docs/images/016.jpg)
![hutool验证码接口](docs/images/017.jpg)
![hutool验证码接口](docs/images/018.jpg)

#### profile:interceptor

> 基于过滤器的验证码校验
![博客文章提交](docs/images/019.jpg)


### 加解密模块示例（framework/framework-examples/examples-crypto）

#### profile:simple

> 加密/解密 工具
![加密/解密 工具](docs/images/039.jpg)

> 摘要计算
![摘要计算](docs/images/040.jpg)


#### profile:api

> api请求，响应加解密
![api请求，响应加解密](docs/images/020.jpg)

### 数据模块示例（framework/framework-examples/examples-data）

#### profile:jpa

> 基于jpa的查询
![api请求，响应加解密](docs/images/021.jpg)


#### profile:mybatis

> 基mybatis的curd
![基mybatis的curd](docs/images/022.jpg)


### 多租户模块示例（framework/framework-examples/examples-tenant）

#### profile:cache

> Spring Cache 多租户演示
![Spring Cache 多租户演示](docs/images/024.jpg)

#### profile:jpa-database

> 多租户用户管理系统(JPA+数据库隔离)
![多租户用户管理系统(JPA+数据库隔离)](docs/images/025.jpg)

> 多租户用户管理系统(JPA+表隔离)
![多租户用户管理系统(JPA+表隔离)](docs/images/026.jpg)


#### profile:mybatis-database

> 多租户用户管理系统(MYBATIS+表隔离)
![多租户用户管理系统(MYBATIS+表隔离)](docs/images/027.jpg)

> 多租户用户管理系统(MYBATIS+数据库隔离)
![多租户用户管理系统(MYBATIS+数据库隔离)](docs/images/028.jpg)

### 调度模块示例（framework/framework-examples/examples-schedule）

#### profile:xxl-job

> xxl-job 任务 示例
![xxl-job 任务 示例](docs/images/045.jpg)

#### profile:powerjob

> powerjob 任务 示例
![powerjob 任务 示例](docs/images/046.jpg)


### web模块示例（framework/framework-examples/examples-web）

#### profile:annotation

> 接口防护 示例
![接口防护 示例](docs/images/029.jpg)

#### profile:cached-body

> 请求体缓存演示
![请求体缓存演示](docs/images/030.jpg)

#### profile:client

> 三种HTTP客户端对比
![三种HTTP客户端对比](docs/images/031.jpg)


#### profile:request-logging

> 请求日志 示例
![请求日志 示例](docs/images/034.jpg)

#### profile:signature

> 签名支付示例 示例
![签名支付示例 示例](docs/images/035.jpg)

#### profile:trace

> Trace API 测试控制台
![Trace API 测试控制台](docs/images/036.jpg)

#### profile:xss

> XSS 防护演示
![XSS 防护演示](docs/images/037.jpg)

#### profile:validation

> 校验异常演示
![校验异常演示](docs/images/048.jpg)
![校验异常演示](docs/images/049.jpg)
![校验异常演示](docs/images/050.jpg)
![校验异常演示](docs/images/051.jpg)

> 校验异常演示(响应式)
![校验异常演示(响应式)](docs/images/052.jpg)

#### profile:totp

> 两步验证
![两步验证](docs/images/032.jpg)

> 博客发布(2fa)
![博客发布(2fa)](docs/images/033.jpg)


### 消息模块示例（framework/framework-examples/examples-message）

#### profile:redis-list

> 基于Redis List的短信消息示例
![基于Redis List的短信消息示例](docs/images/053.jpg)

> 基于 Redis ZSet 的延迟任务队列
![基于 Redis ZSet 的延迟任务队列](docs/images/054.jpg)

## 博客文章

* [004-缓存模块-Caffeine缓存自定义：构建灵活的Spring Boot缓存管理器](docs/blog/004.md)                                                                                  
* [005-缓存模块-Redis自定义缓存：基于Spring Boot的精细化缓存管理实践](docs/blog/005.md)                                                                                
* [006-缓存模块-两级缓存实战：基于 Caffeine + Redis 的多级缓存设计与实现](docs/blog/006.md)
* [007-租户模块-基于 TransmittableThreadLocal 与 TaskDecorator 的租户上下文传递设计](docs/blog/007.md)
* [008-租户模块-基于Caffeine的租户隔离与两级缓存实践](docs/blog/008.md)
* [009-租户模块-基于 Hibernate 的多租户连接提供者设计实战](docs/blog/009.md)
* [010-数据模块-多数据源管理器在 Hibernate 多租户中的应用](docs/blog/010.md)
* [011-数据模块-基于雪花算法的 Hibernate 分布式主键生成器设计与实现](docs/blog/011.md)
* [012-缓存模块-基于 Spring Cache 的缓存操作模版，支持Caffeine缓存, Redis缓存及两级缓存](docs/blog/012.md)
* [013-缓存模块-基于Redis的计数器缓存模板设计——AbstractCounterCacheTemplate 技术解析](docs/blog/013.md)
* [014-web模块-构建可重复读取的请求体：Spring Boot 请求缓存过滤器设计与实现](docs/blog/014.md)
* [015-web模块-基于Spring Boot的HTTP客户端日志与默认配置实战](docs/blog/015.md)
* [016-web模块-基于 MDC 的分布式追踪框架设计与实现](docs/blog/016.md)
* [017-web模块-基于计数器的接口幂等性与访问限流设计实战](docs/blog/017.md)
* [018-web模块-基于AntiSamy的XSS攻击防护过滤器设计与实现](docs/blog/018.md)
* [019-数据模块-MyBatis-Plus 拦截器扩展设计：基于函数式接口与 Spring 自动装配](docs/blog/019.md)
* [020-缓存模块-基于 BeanCreator 的缓存管理器创建器模式设计与实践](docs/blog/020.md)
* [021-数据模块-基于 BaseEnum 的统一枚举处理方案：序列化与 JPA 转换实践](docs/blog/021.md)
* [022-数据模块-基于雪花算法的 MyBatis-Plus 主键生成器设计与实现](docs/blog/022.md)
* [023-数据模块-深入剖析 MyBatis 通用枚举处理器：BaseEnum 与 BaseEnumTypeHandler 的设计与实现](docs/blog/023.md)
* [024-Web模块-基于 AntiSamy 的 Spring Boot XSS 防护实践：从过滤器到反序列化的多层防御](docs/blog/024.md)
* [025-Web模块-基于 Spring Boot 的请求日志过滤器设计与实现](docs/blog/025.md)
* [026-数据模块-基于 MyBatis Plus 的企业级数据访问框架设计与实现](docs/blog/026.md)
* [027-Web模块-基于 Spring MVC 的 API 签名校验拦截器设计与实现](docs/blog/027.md)
* [028-缓存模块-命名缓存：多级个性化缓存配置的设计与实现](docs/blog/028.md)
* [029-公共模块-基于 Jakarta Validation 实现的自定义日期时间格式校验](docs/blog/029.md)
* [030-Web模块-Spring Boot 验证与 OpenAPI 集成实战：从校验规则到文档生成](docs/blog/030.md)
* [031-缓存模块-RedisTemplate工具的租户隔离设计：自动Key前缀机制](docs/blog/031.md)
* [032-缓存模块-基于Redis Bitmap的用户行为统计实战：签到与日活分析](docs/blog/032.md)
* [033-缓存模块-基于 Redisson 的租户隔离 Redis Key 前缀设计](docs/blog/033.md)
* [034-公共模块-基于SpEL的方法参数表达式求值器设计与实现](docs/blog/034.md)
* [035-缓存模块-Redisson 分布式锁实战：可重入锁与阻塞锁的设计与实现](docs/blog/035.md)
* [036-缓存模块-基于 Redis 自定义缓存锁的设计与实现](docs/blog/036.md)
* [037-缓存模块-基于 Guava Striped 的声明式本地锁设计与实现](docs/blog/037.md)
* [038-验证码模块-基于 Hutool 的 Spring Boot 验证码组件设计与实现](docs/blog/038.md)
* [039-验证码模块-天意验证码（Tianai Captcha）Spring Boot 自动配置深度解析](docs/blog/039.md)
* [040-验证码模块-验证码请求过滤器（CaptchaRequestFilter）设计与实现解析](docs/blog/040.md)  d                                                                               
* [041-公共模块-分布式唯一ID生成器设计与实现：一款灵活可扩展的雪花算法框架](docs/blog/041.md)
* [042-数据模块-Mybatis Plus 数据库级租户：基于多数据源路由的动态隔离实现](docs/blog/042.md)
* [043-数据模块-基于 Spring Data JPA 的企业级数据访问层设计——实体、审计、状态与服务抽象](docs/blog/043.md)
* [044-Web模块-基于 Google Authenticator 的 TOTP 双因素认证框架设计与实现](docs/blog/044.md)
* [045-Crypto模块-设计一个可扩展的加解密框架：策略模式与工厂模式实战](docs/blog/045.md)
* [046-Crypto模块-Spring Boot 自动配置进阶：按需装配加解密处理器](docs/blog/046.md)
* [047-Crypto模块-基于 Hutool 的常见加解密算法封装与密钥自动生成](docs/blog/047.md)
* [048-Crypto模块-Spring Boot 请求体自动解密：@Crypto 注解 + RequestBodyAdvice 实现](docs/blog/048.md)
* [049-Crypto模块-前后端混合加密API实战：基于Spring Boot的AES+RSA安全传输方案](docs/blog/049.md)
* [050-功能模块-基于 Redis Bitmap 的高性能签到系统设计](docs/blog/050-.md)
* [051-缓存模块-基于 StringRedisTemplate 的多租户 Key 隔离设计与实践——以 RedisBitmapUtils 为例](docs/blog/051.md)
* [052-核心模块-Java线程池封装实践：`ExecutorServiceHolder` 设计与实现](docs/blog/052.md)
* [053-核心模块-Java枚举缓存与ORM集成实践](docs/blog/053.md)
* [054-核心模块-工厂模式的“Bean工具化”设计：从静态工具到Spring托管Bean的演进](docs/blog/054.md)
* [055-调度模块-Spring动态任务调度框架的设计与实现](docs/blog/055.md)
* [056-调度模块-RunnableDecorator – 任务执行的增强装饰器](docs/blog/056.md)
* [057-调度模块-分布式环境下定时任务的防重复执行方案](docs/blog/057.md)
* [058-调度模块-任务生命周期事件与监听器机制](docs/blog/058.md)
* [059-调度模块-配置驱动的任务仓库 – 从 YAML 加载任务](docs/blog/059.md)
* [060-调度模块-Redisson vs Redis 原生锁：两种分布式锁实现深度对比](docs/blog/060.md)
* [061-调度模块-领域驱动的任务调度架构设计与分层实践](docs/blog/061.md)
* [062-调度模块-任务全生命周期管理 —— 从启动到追踪的完整闭环](docs/blog/062.md)
* [063-调度模块-事件驱动的日志记录与高效查询体系](docs/blog/063.md)
* [064-缓存模块-两级缓存实战：基于 Caffeine 和 Redis 的多级缓存设计与实现](docs/blog/064.md)
* [065-缓存模块-Hibernate二级缓存自定义实现：基于Spring Cache的多级缓存适配器](docs/blog/065.md)
* [066-调度模块-基于Spring Boot的分布式定时任务框架集成：PowerJob与XXL-JOB自动配置解析](docs/blog/066.md)
* [067-公共模块-构建优雅的Java异常处理框架：从错误码到统一响应](docs/blog/067.md)
* [068-公共模块-Spring Boot 全局异常处理与参数校验实战（上）：架构设计与响应封装](docs/blog/068.md)
* [069-公共模块-Spring Boot 全局异常处理与参数校验实战（下）：校验异常精细化处理与 WebFlux 适配](docs/blog/069.md)
* [070-Web模块-Spring MVC TOTP 二次认证拦截器：设计与源码深度解析](docs/blog/070.md)
* [071-验证码模块-基于Spring拦截器的验证码认证设计思想](docs/blog/071.md)
* [072-验证码模块-验证码认证拦截器实现解析与扩展实战](docs/blog/072.md)
* [073-示例-基于Redis分布式锁的定时任务调度实践](docs/blog/073.md)
* [074-示例-基于Redisson的分布式锁在定时任务中的实践与异常模拟](docs/blog/074.md)
* [075-示例-基于自定义的定时任务框架实战详解](docs/blog/075.md)
* [076-核心模块-构建优雅的Java异常处理框架：从错误码到全局异常处理](docs/blog/076.md)
* [077-消息模块-基于 Redis List 的轻量级消息队列框架设计解析](docs/blog/077.md)
* [078-消息模块-基于 Redis ZSet 的延迟消息队列设计与实现](docs/blog/078.md)
* [079-消息模块-基于 Redis Stream 的高可靠消息队列实现——Spring Data Redis 实战解析](docs/blog/079.md)  
* [080-缓存模块-Spring Cache缓存键前缀设计与租户隔离实践](docs/blog/080.md)