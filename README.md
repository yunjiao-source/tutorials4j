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

> 缓存模版
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

