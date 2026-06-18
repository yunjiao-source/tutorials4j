

# tutorials4j

Java 教程项目

## 项目简介

tutorials4j 是一个基于 Spring Boot 的 Java 教程项目，提供丰富的示例代码和实用的企业级框架组件。

## 核心模块

| 模块 | 说明 |
|------|------|
| framework | 核心框架，包含缓存、验证码、加解密、数据等功能 |
| framework-examples | 各功能模块的使用示例 |
| assembly | 集成模块，组合多个框架组件 |
| java21 | Java 21 新特性示例 |
| springboot3 | Spring Boot 3 示例 |
| springboot4 | Spring Boot 4 示例 |
| springcloud | Spring Cloud 示例 |

## 框架功能

### 缓存模块 (framework-cache)
- Caffeine 本地缓存
- Redis 分布式缓存
- Redisson 分布式锁
- 多级缓存支持

### 验证码模块 (framework-captcha)
- Hutool 图形验证码
- Tianai 行为验证码（滑块、旋转、点击等）
- 验证码 Web 防护

### 加解密模块 (framework-crypto)
- 对称加密（AES、DES、SM4）
- 非对称加密（RSA、SM2）
- 摘要算法（SHA256、SM3、HMac）

### 数据模块 (framework-data)
- JPA/Hibernate 支持
- MyBatis-Plus 支持
- 多数据源路由
- 雪花算法 ID 生成

### 功能模块 (framework-feature)
- 签到系统
- 任务调度
- TOTP 两步验证

## 技术栈

- Java 21
- Spring Boot 3/4
- Spring Cloud
- Redis
- MySQL
- Maven

## 快速开始

```bash
# 克隆项目
git clone https://gitee.com/yunjiao-source/tutorials4j.git

# 构建项目
cd tutorials4j
mvn clean install -DskipTests
```

## 示例运行

各示例模块通过 Spring Profile 区分运行：

```bash
# 运行缓存示例
java -jar examples-cache.jar --spring.profiles.active=cacheable

# 运行验证码示例
java -jar examples-captcha.jar --spring.profiles.active=simple
```

## License

Apache License 2.0