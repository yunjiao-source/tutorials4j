# springboot3-pay

## 代码分析总结

### 整体概述

这是一个基于 **Spring Boot 3** 的**统一支付平台**项目，采用**策略模式**设计，实现了对**微信支付、支付宝、银联支付**三种支付渠道的统一封装，提供支付、退款、订单查询、回调处理等核心功能。目前 **微信支付部分已完整实现**，支付宝和银联支付部分为待实现的骨架代码。

---

### 目录结构

```
tutorials4j.springboot3/
├── model/                    # 数据模型
│   ├── Constants.java        # 常量定义（操作状态/消息）
│   ├── PayChannel.java       # 支付渠道枚举（微信/支付宝/银联）
│   ├── PayStatus.java        # 支付状态枚举（待支付/成功/失败/取消/退款）
│   ├── TradeMethod.java      # 交易方式枚举（支付/退款）
│   └── TradeType.java        # 交易类型枚举（APP/JSAPI/NATIVE/H5）
├── request/                  # 请求对象
│   ├── BaseRequest.java      # 公共请求参数（商户配置信息）
│   ├── PayRequest.java       # 支付请求参数
│   └── RefundRequest.java    # 退款请求参数
├── response/                 # 响应对象
│   ├── PayResponse.java      # 支付响应
│   ├── QueryResponse.java    # 查询响应
│   ├── CloseResponse.java    # 关单响应
│   ├── RefundResponse.java   # 退款响应
│   ├── NotifyRequest.java    # 回调请求（包装HttpServletRequest）
│   └── NotifyResponse.java   # 回调处理响应
├── service/                  # 服务层
│   └── PayService.java       # 统一支付服务（对外入口）
├── strategy/                 # 策略层
│   ├── PayStrategy.java           # 支付策略接口
│   ├── AbstractPayStrategy.java   # 抽象策略类（模板方法）
│   ├── PayStrategyFactory.java    # 策略工厂
│   ├── WechatPayStrategy.java     # 微信支付实现（完整）
│   ├── AlipayStrategy.java        # 支付宝实现（骨架）
│   └── UnionPayStrategy.java      # 银联实现（骨架）
├── DemoRunner.java           # 启动示例（CommandLineRunner）
└── SpringBoot3PayApplication.java # 主启动类
```

---

### 核心功能模块

| 功能 | 说明 | 微信支付实现情况 |
|------|------|----------------|
| **统一支付** | 支持 APP/JSAPI/NATIVE/H5 四种交易类型 | ✅ 完整实现 |
| **订单查询** | 根据商户订单号查询支付状态 | ✅ 完整实现 |
| **关闭订单** | 关闭未支付的订单 | ✅ 完整实现 |
| **申请退款** | 发起退款请求 | ✅ 完整实现 |
| **退款查询** | 查询退款状态 | ✅ 完整实现 |
| **回调处理** | 处理支付/退款异步通知 | ✅ 完整实现 |

---

### 设计亮点

#### 1. 策略模式 + 工厂模式

```java
// 策略接口定义统一操作
PayStrategy → pay() / queryOrder() / close() / refund() / queryRefund() / handleNotify()

// 工厂根据渠道获取对应策略
PayStrategyFactory → getStrategy(PayChannel.WECHAT_PAY)

// 统一服务入口
PayService.pay(request) → 委托给对应策略执行
```

**优点**：
- 新增支付渠道只需实现 `PayStrategy` 并注册即可，符合**开闭原则**
- 业务层与具体支付实现解耦

#### 2. 模板方法模式

```java
AbstractPayStrategy {
    public PayResponse pay(PayRequest req) {
        log.info(...);
        return doPay(req);  // 子类实现
    }
}
```

**优点**：
- 统一异常处理、日志记录
- 子类只需关注核心业务逻辑

#### 3. 分层清晰

| 层级 | 职责 |
|------|------|
| `request/` | 定义各接口输入参数 |
| `response/` | 定义各接口输出参数 |
| `model/` | 枚举与常量 |
| `service/` | 对外统一入口 |
| `strategy/` | 各渠道具体实现 |

---

### 微信支付实现细节（已完成）

#### 技术栈
- 微信支付 SDK：`com.wechat.pay.java`
- 证书方式：`RSAAutoCertificateConfig`（自动获取平台证书）

#### 各交易类型实现

| 交易类型 | 使用SDK类 | 返回内容 |
|----------|-----------|----------|
| APP | `AppServiceExtension` | `payParams`（App调起参数） |
| JSAPI | `JsapiServiceExtension` | `payParams`（JS调起参数） |
| NATIVE | `NativePayService` | `codeUrl`（二维码链接） |
| H5 | `H5Service` | `codeUrl`（H5跳转URL） |

#### 金额处理

```java
// 订单金额（元）→ 微信支付（分）
request.getAmount().multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue()
```

#### 回调处理

- 支持**支付结果通知**和**退款结果通知**两种类型
- 使用 `NotificationParser` 验签并解析通知内容

---

### 待完善模块

| 模块 | 当前状态 | 需要实现的内容 |
|------|----------|----------------|
| **AlipayStrategy** | 骨架代码 | 对接支付宝SDK：支付、查询、关闭、退款、回调 |
| **UnionPayStrategy** | 骨架代码 | 对接银联SDK：同上 |
| `BaseRequest` 中的 `publicKey` | 已定义但未使用 | 支付宝可能需要 |
| `PayRequest` 中部分字段 | `attach`、`h5Type` 等 | 确认各渠道是否支持 |
| 单元测试 | 缺失 | 覆盖各策略的核心方法 |

---

### 使用示例

```java
// 1. 构建支付请求
PayRequest request = new PayRequest();
request.setPayChannel(PayChannel.WECHAT_PAY);
request.setTradeType(TradeType.APP);
request.setOrderNo("ORDER_123456");
request.setAmount(new BigDecimal("100")); // 1元
request.setDescription("测试商品");
// ... 设置商户配置（appId、mchId、私钥等）

// 2. 发起支付
PayResponse response = payService.pay(request);

// 3. 处理响应
if (response.isSuccess()) {
    // 返回前端调起支付所需参数
    Map<String, Object> params = response.getPayParams();
} else {
    // 支付失败
    String error = response.getErrorMsg();
}
```

---

### 可优化建议

1. **配置外部化**：商户证书信息建议使用配置文件或配置中心，避免硬编码
2. **重试机制**：对网络波动导致的失败增加重试（如退款、查询接口）
3. **幂等处理**：回调处理需支持幂等，防止重复通知导致异常
4. **监控告警**：增加支付失败率、耗时等指标监控
5. **支付宝/银联补全**：尽快实现另外两个渠道的具体逻辑

---

### 总结

这是一个**设计良好、扩展性强**的统一支付SDK项目：

| 维度 | 评价 |
|------|------|
| 架构设计 | 策略模式 + 工厂模式 + 模板方法，清晰易扩展 |
| 代码质量 | 日志完整、异常处理规范、注解使用恰当 |
| 微信支付 | 功能完整，覆盖全部常用交易类型 |
| 扩展性 | 新增渠道仅需实现接口，无需修改现有代码 |
| 文档 | 类/方法注释完整，DemoRunner提供使用示例 |

**适合作为**：企业级项目支付模块的基础框架，或作为学习支付系统设计的参考项目。