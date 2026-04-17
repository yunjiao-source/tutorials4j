# springboot3-custom-annotation

## 代码整体分析

该项目是一个 **Spring Boot 3** 应用，演示了如何通过**自定义注解 + AOP 切面**实现参数校验、权限控制和接口日志记录。代码结构清晰，注解定义规范，切面逻辑职责单一，适合作为学习或项目中的基础组件。

---

## 1. 各组件详解

### 1.1 `@CheckParam` 参数校验
- **注解定义** (`CheckParam.java`)  
  作用于**字段**，提供三个属性：
    - `notNull`：是否非空校验（默认 false）
    - `minLength` / `maxLength`：字符串长度限制（默认不限制）
    - `message`：校验失败时的提示信息（默认使用字段名）

- **切面实现** (`CheckParamAspect.java`)
    - 切入点：`execution(* tutorials4j.springboot3.CheckParamController.*(..))`，仅拦截指定 Controller 的所有方法。
    - 环绕通知中获取方法参数，遍历每个参数对象的字段，通过反射读取 `@CheckParam` 注解并进行校验。
    - 支持：
        - 非空校验（对象为 null 或字段值为 null）
        - 字符串长度校验（仅当字段值为 `String` 类型时）
    - 校验失败抛出 `RuntimeException`，携带自定义或默认的提示信息。

- **使用示例** (`CheckParamController.UserDTO`)  
  对 `userId`、`userName`、`phone` 字段分别配置了不同的校验规则。

> ✅ **优点**：轻量级，不依赖 Hibernate Validator，适合简单场景。  
> ⚠️ **潜在问题**：
> - 只支持字符串长度校验，对集合、数组、数字范围等常见校验未实现。
> - 切入点硬编码为 `CheckParamController`，复用性差；可改为 `@annotation(checkParam)` 方式，但注解作用于字段，需配合方法参数上的 `@Valid` 风格或自行设计。
> - 反射调用 `field.setAccessible(true)` 有一定性能开销，但在 Web 请求中可忽略。
> - 抛出 `RuntimeException` 会返回 500 错误，建议配合 `@ControllerAdvice` 统一返回 400 类错误码。

---

### 1.2 `@NeedPermission` 权限校验
- **注解定义** (`NeedPermission.java`)  
  作用于**方法**，属性 `value()` 为字符串数组，表示所需的权限编码（如 `"user:add"`）。

- **切面实现** (`NeedPermissionAspect.java`)
    - 切入点：`@annotation(needPermission)`，自动匹配所有标注了该注解的方法。
    - 环绕通知中获取注解的 `value()`，与**模拟的用户权限列表**（`["user:query", "order:query"]`）进行比对。
    - 若当前用户不拥有全部所需权限，则抛出 `RuntimeException`；否则放行。

- **使用示例** (`NeedPermissionController`)  
  展示了单权限、多权限以及无权限访问的情况。

> ✅ **优点**：注解方式声明权限，代码可读性强。  
> ⚠️ **潜在问题**：
> - 用户权限列表是硬编码的，实际项目中应从 SecurityContext、Token 或数据库中动态获取。
> - 抛出 `RuntimeException` 同样缺乏统一异常处理。
> - 权限校验逻辑为“与”（需同时拥有所有权限），不支持“或”关系，可考虑扩展。

---

### 1.3 `@RestLogger` 接口日志
- **注解定义** (`RestLogger.java`)  
  作用于**方法**，提供三个配置：
    - `value`：接口功能描述
    - `printParam`：是否打印请求参数（默认 true）
    - `printResult`：是否打印响应结果（默认 true）

- **切面实现** (`RestLoggerAspect.java`)
    - 切入点：`@annotation(restLogger)`。
    - 环绕通知中：
        - 记录开始时间
        - 从 `RequestContextHolder` 获取 `HttpServletRequest`，打印 URL、HTTP 方法、类名.方法名
        - 根据配置打印请求参数（`Arrays.toString(joinPoint.getArgs())`）
        - 执行目标方法
        - 根据配置打印响应结果
        - 打印接口总耗时
    - 异常时记录错误日志并重新抛出。

- **使用示例** (`RestLoggerController`)  
  演示了默认配置和不打印响应结果两种用法。

> ✅ **优点**：日志格式统一，支持开关，便于调试和监控。  
> ⚠️ **潜在问题**：
> - 请求参数使用 `Arrays.toString()`，如果参数是复杂对象或二进制数据，输出可能不友好。可改用 JSON 序列化（如 `ObjectMapper`）。
> - `RequestContextHolder` 在非 Web 环境（如定时任务调用）会返回 `null`，可增加判空保护。
> - 日志中打印了完整请求参数，注意敏感信息（密码、token）的脱敏处理。

---

## 2. 整体设计评价

| 方面 | 评价 |
|------|------|
| **注解设计** | 属性命名清晰，支持常用校验/日志开关，符合直觉。 |
| **AOP 使用** | 环绕通知正确管理了目标方法的执行和异常，切入点表达式准确。 |
| **代码规范** | 包名、类名、方法命名符合 Java 惯例，注释完整（含作者、说明）。 |
| **Spring Boot 3 兼容性** | 使用 `jakarta.servlet` 包，符合 Spring Boot 3 要求。 |
| **扩展性** | 各切面独立，可轻松添加新的校验规则或日志字段。 |

---

## 3. 改进建议

### 3.1 统一异常处理
所有切面抛出的 `RuntimeException` 应通过 `@RestControllerAdvice` 转换为标准的 HTTP 状态码（如 400、403）和错误体。

```java
@ExceptionHandler(RuntimeException.class)
public ResponseEntity<ErrorResponse> handle(RuntimeException e) {
    if (e.getMessage().contains("不能为空") || e.getMessage().contains("长度不能")) {
        return ResponseEntity.badRequest().body(new ErrorResponse(400, e.getMessage()));
    }
    if (e.getMessage().contains("无权限")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(...);
    }
    return ResponseEntity.internalServerError().body(...);
}
```

### 3.2 增强 `@CheckParam`
- 支持更多数据类型：集合（`Collection` 非空/大小）、数字（`@Min`/`@Max`）、正则（`@Pattern`）。
- 支持嵌套对象校验（类似 `@Valid`）。
- 将切入点改为匹配任何带有 `@CheckParam` 注解的字段所在的 Bean 方法，可通过自定义 `@ValidCheck` 注解标记需要校验的方法参数。

### 3.3 权限切面集成 Spring Security
实际项目推荐使用 Spring Security 的 `@PreAuthorize`，或从 `SecurityContextHolder` 获取用户权限：

```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
List<String> userPermissions = auth.getAuthorities().stream()
    .map(GrantedAuthority::getAuthority)
    .collect(Collectors.toList());
```

### 3.4 日志切面优化
- 使用 JSON 格式打印参数和结果，便于日志采集。
- 增加 `@RestLogger(printParam = false, printResult = false)` 时，只打印请求 URL 和耗时，适合高流量接口。
- 对敏感字段（如密码、身份证）自动脱敏。

### 3.5 性能考量
- `CheckParamAspect` 中的反射可以缓存 `Field` 和 `CheckParam` 注解，避免每次请求都重新调用 `getDeclaredFields()`（可使用 `ConcurrentHashMap` 按 Class 缓存）。
- 日志切面中获取 `MethodSignature` 和 `Method` 也有一定开销，但通常可忽略。

---

## 4. 总结

该项目是一个**简洁且实用的 Spring Boot 3 自定义注解 + AOP 示例**，涵盖了日常开发中最常见的三个横切关注点：**参数校验**、**权限控制**、**接口日志**。代码可读性强，易于扩展，适合直接集成到小型项目或作为教学案例。若需用于生产环境，建议结合上述改进点增强健壮性和安全性。