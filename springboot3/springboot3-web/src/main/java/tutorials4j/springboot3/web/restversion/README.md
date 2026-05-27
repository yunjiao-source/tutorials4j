该代码实现了一个基于 Spring Boot 3 的 REST API 版本控制方案，支持两种策略：**请求头（Header）** 和 **路径（Path）**，并通过配置进行切换。下面对各文件功能进行分析：

---

## 1. 核心注解与控制器

### `ApiVersion.java`
- 自定义注解，标注在 `Controller` 类或方法上。
- 属性 `value()` 用于声明 API 版本号（`double` 类型，默认 1.0）。
- 运行时保留，作用于方法。

### `DemoController.java`
- 示例控制器，映射路径 `/api/cursorUsers`。
- 两个 `@GetMapping("/cursorUsers")` 方法，分别标注 `@ApiVersion(1.0)` 和 `@ApiVersion(2.0)`。
- 依赖自定义的 `RequestMappingHandlerMapping` 来区分两个相同 URL 但不同版本的方法。

---

## 2. 基于请求头的版本控制（Header 模式）

### `VersionHeaderRequestCondition.java`
- 实现 `RequestCondition` 接口，负责**匹配条件**。
- 构造函数接收接口声明的版本号 `apiVersion`。
- `getMatchingCondition` 方法：
    - 从 `HttpServletRequest` 的 Header 中读取 `X-API-Version` 值。
    - 若存在且与 `apiVersion` 精确相等，则匹配成功。
    - 若不存在，且 `apiVersion == 1.0`，则默认匹配（兼容无版本请求）。
- `combine` 方法：合并类与方法上的版本，优先使用方法版本。
- `compareTo` 方法：多匹配时，按版本号降序排序（高版本优先）。

### `VersionHeaderRequestMappingHandlerMapping.java`
- 继承 `RequestMappingHandlerMapping`，重写：
    - `getCustomTypeCondition`：提取类上的 `@ApiVersion` 注解。
    - `getCustomMethodCondition`：提取方法上的注解。
- 将 `VersionHeaderRequestCondition` 附加到 `RequestMappingInfo` 中。

---

## 3. 基于路径的版本控制（Path 模式）

### `VersionPathFilter.java`
- 继承 `OncePerRequestFilter`，优先级最高（`Ordered.HIGHEST_PRECEDENCE`）。
- 功能：**URL 重写**。
    - 正则匹配请求 URI 开头的 `/v数字(.数字)?`（如 `/v1.0/api/cursorUsers`）。
    - 将版本前缀剥离后，生成新的 URI（如 `/api/cursorUsers`）。
    - 使用 `HttpServletRequestWrapper` 重写 `getRequestURI()` 返回新 URI。
    - 同时将原始 URI 存入 `request` 属性 `originalUri`，供后续条件匹配使用。
- 作用：让 `@RequestMapping("/api/cursorUsers")` 仍能正常映射，同时保留原始路径中的版本信息。

### `VersionPathRequestCondition.java`
- 类似 Header 版本的条件类，但版本信息**从原始 URI** 中提取。
- `getMatchingCondition`：
    - 从 `request` 属性中获取 `originalUri`（由 Filter 存入）。
    - 正则提取版本号，与 `apiVersion` 精确匹配。
    - 若无版本前缀，默认匹配 1.0 版本。
- `combine` 和 `compareTo` 逻辑与 Header 版本一致。

### `VersionPathRequestMappingHandlerMapping.java`
- 同样继承 `RequestMappingHandlerMapping`，附加 `VersionPathRequestCondition` 到方法/类。

---

## 4. 配置类 `WebMvcConfig.java`

- 使用 `@ConditionalOnProperty` 根据配置项 `rest.version` 选择启用哪种模式：
    - `rest.version=header`（默认或缺失）：启用 **Header 模式**。
        - 注册 `VersionHeaderRequestMappingHandlerMapping`。
    - `rest.version=path`：启用 **Path 模式**。
        - 注册 `VersionPathRequestMappingHandlerMapping` 并设置 `order = -1`（高优先级）。
        - 注册 `VersionPathFilter` Bean。

---

## 5. 整体工作流程

### Header 模式
1. 客户端请求：`GET /api/cursorUsers`，Header 中添加 `X-API-Version: 2.0`。
2. Spring 调用 `VersionHeaderRequestMappingHandlerMapping` 匹配请求。
3. `VersionHeaderRequestCondition` 读取 Header 中的版本号，与每个方法上的版本比较。
4. 匹配到版本 2.0 的方法，执行 `getUsersV2()`。

### Path 模式
1. 客户端请求：`GET /v2.0/api/cursorUsers`。
2. `VersionPathFilter` 将 URI 重写为 `/api/cursorUsers`，并保存原始 URI 到 `originalUri`。
3. `VersionPathRequestMappingHandlerMapping` 匹配时，`VersionPathRequestCondition` 从 `originalUri` 中提取版本号 `2.0`。
4. 匹配到版本 2.0 的方法，执行 `getUsersV2()`。

---

## 6. 潜在问题与改进建议

- **Header 模式默认版本问题**  
  当请求无 `X-API-Version` 头时，仅版本 1.0 的方法被匹配。若存在更高版本的方法但无默认版本，可能导致请求失败。可以考虑支持“最低版本”或“版本范围”匹配。

- **版本号类型**  
  使用 `double` 可能导致浮点数精度问题（如 1.1 和 1.10 被视为不同版本）。建议改用 `String` 或语义化版本（如 `"1.2.3"`），并实现更灵活的版本比较逻辑。

- **Path 模式与 Swagger/OpenAPI 兼容性**  
  过滤后的 URI 不再是原始请求路径，可能影响 API 文档生成工具。需要额外适配。

- **重复代码**  
  `VersionHeaderRequestCondition` 和 `VersionPathRequestCondition` 逻辑高度相似，可抽取公共基类。

- **`@ApiVersion` 在类上的处理**  
  当前实现支持类级别注解，但 `DemoController` 未演示。若类上有 `@ApiVersion(1.0)`，方法上未标注，则方法默认继承类版本。

- **线程安全**  
  `RequestCondition` 实现中未使用共享可变状态，是线程安全的。

---

## 7. 总结

该代码提供了一个**可配置、扩展性强**的 REST API 版本控制方案，充分利用了 Spring MVC 的 `RequestMappingHandlerMapping` 扩展点。通过 `RequestCondition` 实现了版本匹配逻辑，并通过 Filter + Wrapper 实现路径重写，是生产环境可参考的实践示例。