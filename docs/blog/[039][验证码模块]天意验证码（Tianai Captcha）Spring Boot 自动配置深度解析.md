# [039][验证码模块]天意验证码（Tianai Captcha）Spring Boot 自动配置深度解析

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

## 1. 概述

天意验证码是一款开源的 Java 图形验证码库，支持滑动、旋转、拼图、文字点选等多种验证形式。本文基于一套完整的 Spring Boot 自动配置实现，分析其如何将天意验证码无缝集成到 Spring Boot 项目中，并提供统一的 `CaptchaService` 接口、Redis 缓存支持、灵活的参数配置以及 REST API。

文章涵盖：
- 整体架构与设计模式
- 核心组件职责分析
- 验证码生成与校验流程
- 配置体系与扩展点
- 使用示例

## 2. 整体架构

项目采用分层 + 工厂模式设计，主要模块如下：

```
┌─────────────────────────────────────────────────────────────┐
│                      Spring Boot AutoConfiguration           │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ TianaiCaptchaConfiguration（配置类）                      ││
│  │   - 生成各类型 CaptchaService Bean                       ││
│  │   - 生成 RedisCacheStore、ImageCaptchaApplication        ││
│  │   - 生成 TianaiCaptchaController                         ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       Service Layer                          │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────────────┐ │
│  │SliderCaptcha │ │RotateCaptcha │ │WordImageClickCaptcha │ │
│  │Service       │ │Service       │ │Service                │ │
│  └──────────────┘ └──────────────┘ └──────────────────────┘ │
│  ┌──────────────┐                                            │
│  │ConcatCaptcha │  extends AbstractCaptchaService           │
│  │Service       │                                            │
│  └──────────────┘                                            │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                 Tianai Captcha Core                          │
│  ┌──────────────────┐  ┌──────────────────────────────────┐ │
│  │ImageCaptcha      │  │RedisCacheStore                   │ │
│  │Application       │  │(implements CacheStore)           │ │
│  └──────────────────┘  └──────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│               Cache & Web Layer                              │
│  ┌──────────────────┐  ┌──────────────────────────────────┐ │
│  │GraphicCaptcha    │  │TianaiCaptchaController           │ │
│  │CacheTemplate     │  │  /captcha/tianai/gen             │ │
│  └──────────────────┘  │  /captcha/tianai/check           │ │
│                        └──────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## 3. 核心组件分析

### 3.1 自动配置类 `TianaiCaptchaConfiguration`

该配置类采用 `@Configuration(proxyBeanMethods = false)`，提升启动效率。主要职责：

1. **创建各类型验证码服务 Bean**  
   - `rotateCaptchaService`、`sliderCaptchaService`、`wordImageClickCaptchaService`、`concatCaptchaService`  
   - 每个服务都接收 `ImageCaptchaApplication` 和根据配置生成的 `TianAiCaptchaGenerateParamBuilder`。

2. **配置图片资源定制器**  
   `ImageResourceTACBuilderCustomizer` 为所有验证码类型添加默认的背景图和模板图（从 classpath 加载）。

3. **创建 Redis 缓存存储**  
   `RedisCacheStore` 适配天意验证码的 `CacheStore` 接口，底层使用 `GraphicCaptchaCacheTemplate`（多级缓存模板）。

4. **创建 `ImageCaptchaApplication`**  
   使用 `TACBuilder` 构建天意核心应用，添加默认模板、设置缓存存储，并应用所有 `TACBuilderCustomizer`。

5. **创建控制器**  
   `TianaiCaptchaController` 暴露生成和校验接口。

### 3.2 验证码服务抽象与实现

所有具体验证码服务继承自 `AbstractCaptchaService`，该类实现了统一的 `draw()` 和 `verify()` 逻辑：

- **`draw()`**  
  调用 `imageCaptchaApplication.generateCaptcha(builder.createGenerateParam())`，将返回的 `ImageCaptchaVO` 转换为 `Map<String, Object>`，包含图片 URL、宽高、ID 等信息。

- **`verify()`**  
  将前端传来的 JSON 轨迹字符串反序列化为 `ImageCaptchaTrack`，调用 `imageCaptchaApplication.matching()` 完成校验。

每个子类只需实现 `getCategory()` 返回对应的 `CaptchaCategory` 枚举值，实现多态。

### 3.3 缓存层适配

`RedisCacheStore` 实现了天意验证码的 `CacheStore` 接口，核心方法：

| 方法 | 实现逻辑 |
|------|----------|
| `getCache(key)` | 从 `GraphicCaptchaCacheTemplate` 获取 JSON 字符串，反序列化为 `AnyMap` |
| `getAndRemoveCache(key)` | 获取后删除 |
| `setCache(...)` | 序列化后存入缓存（忽略过期时间，由模板自行管理） |
| `incr` / `getLong` | 不支持，直接抛异常（天意验证码中某些场景需要计数器，但本实现未使用） |

`SimpleImageCaptchaApplication` 重写了 `getKey(id)` 直接返回原始 id，避免了天意默认的 `captcha:` 前缀，以便与现有缓存模板兼容。

### 3.4 参数构建器 `TianAiCaptchaGenerateParamBuilder`

该构建器将配置属性（`TianaiOptions`）转换为天意验证码的 `GenerateParam`，支持以下参数：

- `backgroundFormatName` / `templateFormatName` – 图片格式
- `obfuscate` – 是否混淆
- `type` – 验证码类型（SLIDER / ROTATE / WORD_IMAGE_CLICK / CONCAT）
- `backgroundImageTag` / `templateImageTag` – 资源标签
- `fontTag` – 字体标签（用于文字点选）
- `tolerant` – 容差（旋转/滑动）
- `clickInterferenceCount` / `clickCheckClickCount` – 点选验证码干扰项和正确点击数量

通过 `createGenerateParam()` 构建并注入天意 API。

### 3.5 控制器 REST API

`TianaiCaptchaController` 提供两个端点：

| 路径 | 方法 | 说明 |
|------|------|------|
| `/captcha/tianai/gen` | GET | 生成验证码，可选参数 `type` 指定验证码类型，返回图片及轨迹初始化数据 |
| `/captcha/tianai/check` | POST | 校验验证码，请求体 `{id, data}`，`data` 为前端采集的轨迹 JSON |

返回值使用天意的 `ApiResponse` 统一封装。

### 3.6 配置属性体系

`TianaiCaptchaProperties` 绑定配置前缀（例如 `tutorials4j.captcha.tianai`），结构如下：

```yaml
tutorials4j:
  captcha:
    tianai:
      common:               # 公共配置
        background-format-name: jpeg
        template-format-name: png
        obfuscate: false
        tolerant: 0.05
      slider:               # 滑动验证码专用配置（会合并 common）
        background-image-tag: "slider_bg"
      rotate:
        tolerant: 0.1
      word-image-click:
        click-check-click-count: 3
      concat: {}
```

每个验证码类型配置项都会与 `common` 合并，专用配置优先级更高。

## 4. 工作流程

### 4.1 验证码生成流程

1. 前端请求 `/captcha/tianai/gen?type=SLIDER`
2. 控制器根据 `type` 从 `CaptchaServiceFactory` 获取对应的 `SliderCaptchaService`
3. 调用 `service.draw()` → 内部调用 `imageCaptchaApplication.generateCaptcha()`
4. 天意核心生成验证码，将轨迹数据存入缓存（通过 `RedisCacheStore`）
5. 返回图片 URL 及验证码 ID 等信息给前端

### 4.2 验证码校验流程

1. 前端完成交互后，将轨迹数据（JSON）和 ID 提交到 `/captcha/tianai/check`
2. 控制器调用 `imageCaptchaApplication.matching(id, track)`
3. 天意核心从缓存读取正确的轨迹数据，与用户提交的轨迹匹配
4. 匹配成功则删除缓存中的验证码，返回成功；否则返回失败信息

### 4.3 缓存数据流

```
生成时: ImageCaptchaApplication → RedisCacheStore.setCache() → GraphicCaptchaCacheTemplate.put()
校验时: ImageCaptchaApplication → RedisCacheStore.getAndRemoveCache() → 比较后删除
```

## 5. 扩展点与定制化

### 5.1 添加自定义图片资源

实现 `TACBuilderCustomizer` 接口，在 `customiz(TACBuilder builder)` 方法中调用 `builder.addResource(...)`。例如：

```java
@Component
public class MyResourceCustomizer implements TACBuilderCustomizer {
    @Override
    public void customiz(TACBuilder builder) {
        builder.addResource("SLIDER", new Resource("classpath", "my-images/bg.jpg"));
    }
}
```

Spring 会自动将其注入到 `ImageCaptchaApplication` 的构建过程中。

### 5.2 修改缓存实现

默认使用 `GraphicCaptchaCacheTemplate`（基于多级缓存）。可以自行提供另一个 `CacheStore` 的 Bean，框架会优先使用（`@ConditionalOnMissingBean`）。

### 5.3 调整验证码参数

在 application.yml 中配置 `tutorials4j.captcha.tianai` 下的各项参数即可，无需修改代码。

## 6. 使用示例

### 6.1 引入依赖（假设）

```xml
        <dependency>
            <groupId>yunjiao-source.tutorials4j.framework</groupId>
            <artifactId>cache-spring-boot-starter</artifactId>
            <version>${revision}</version>
        </dependency>

        <dependency>
            <groupId>yunjiao-source.tutorials4j.framework</groupId>
            <artifactId>captcha-spring-boot-starter</artifactId>
            <version>${revision}</version>
        </dependency>
```

### 6.2 配置 application.yml

```yaml
tutorials4j:
  captcha:
    tianai:
      common:
        tolerant: 0.08
      slider:
        background-image-tag: "custom_slider"
      rotate:
        tolerant: 0.12
```

### 6.3 前端调用示例

```javascript
// 生成滑动验证码
fetch('/captcha/tianai/gen?type=SLIDER')
  .then(res => res.json())
  .then(data => {
    const id = data.data.id;
    const bgImg = data.data.backgroundImage;
    // 渲染 UI，收集轨迹...
  });

// 校验
fetch('/captcha/tianai/check', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({id: id, data: trackData})
})
.then(res => res.json())
.then(res => {
  if (res.success) alert('验证通过');
});
```

### 6.4 后端获取服务

```java
@Autowired
private CaptchaServiceFactory captchaServiceFactory;

public void demo() {
    CaptchaService service = captchaServiceFactory.findService("SLIDER");
    Map<String, Object> captchaData = service.draw();
    boolean ok = service.verify("captcha-id", "{\"x\":100,\"y\":200}");
}
```

## 7. 总结

该自动配置实现具有以下特点：

- **开箱即用** – 只需引入 Starter 并配置，即可获得完整的验证码 REST API。
- **高性能缓存** – 适配多级缓存模板，支持 Redis 等分布式缓存。
- **灵活配置** – 支持全局公共配置及各验证码类型独立配置，合并逻辑清晰。
- **易扩展** – 通过 `TACBuilderCustomizer` 可轻松定制图片资源、验证码行为。
- **统一服务接口** – 通过 `CaptchaServiceFactory` 屏蔽底层差异，方便业务层调用。
