# [069][公共模块]Spring Boot 全局异常处理与参数校验实战（下）：校验异常精细化处理与 WebFlux 适配

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

上篇我们搭建了异常处理的基础骨架，本篇聚焦于校验异常的精细化处理，同时对比 Servlet 容器与 WebFlux 响应式环境下的实现差异，并分享一些工程化最佳实践。

---

## 一、校验异常处理器 `GlobalValidationExceptionHandler`

该处理器专门拦截三种校验异常，并将错误字段提取为 `Map<String, String>`，最终封装进 `Result.fieldErrors` 中。

### 1. 处理 `MethodArgumentNotValidException`

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public Result<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                 HttpServletRequest request) {
    return handleWebBindException(ex, request);
}
```

实际处理逻辑由 `handleWebBindException` 完成，因为 `BindException` 和 `MethodArgumentNotValidException` 都继承自 `Exception` 且都包含 `BindingResult`，可共用提取逻辑。

### 2. 处理 `BindException`

```java
@ExceptionHandler(BindException.class)
public Result<Void> handleWebBindException(BindException ex, HttpServletRequest request) {
    Map<String, String> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                    FieldError::getField,
                    fieldError -> fieldError.getDefaultMessage() == null ? "无效值" : fieldError.getDefaultMessage(),
                    (msg1, msg2) -> msg1
            ));
    return buildValidationErrorResult(fieldErrors, request);
}
```

- 使用 `getFieldErrors()` 获取所有字段错误。
- 通过 `Collectors.toMap` 聚合，如果同一字段有多个错误（一般不会），取第一个。
- 默认消息为 "无效值"，防止空指针。

### 3. 处理 `ConstraintViolationException`

```java
@ExceptionHandler(ConstraintViolationException.class)
public Result<Void> handleConstraintViolation(ConstraintViolationException ex,
                                              HttpServletRequest request) {
    Map<String, String> fieldErrors = ex.getConstraintViolations()
            .stream()
            .collect(Collectors.toMap(
                    violation -> violation.getPropertyPath().toString(),
                    violation -> violation.getMessage() == null ? "无效值" : violation.getMessage(),
                    (msg1, msg2) -> msg1
            ));
    return buildValidationErrorResult(fieldErrors, request);
}
```

- `ConstraintViolation` 的 `propertyPath` 通常包含完整路径（如 `method.param`），这里直接转为字符串，可根据需要截取最后一段。

### 4. 统一构建方法

```java
private Result<Void> buildValidationErrorResult(Map<String, String> fieldErrors,
                                                HttpServletRequest request) {
    BaseRuntimeException exception = BaseErrorCode.VALIDATION_FAILED.throwed();
    return GlobalExceptionHandler.resolveException(exception, request.getRequestURI())
            .fieldErrors(fieldErrors);
}
```

- 通过 `BaseErrorCode.VALIDATION_FAILED.throwed()` 构造一个业务异常（错误码为校验失败）。
- 复用 `GlobalExceptionHandler.resolveException` 填充基础信息，再链式调用 `fieldErrors` 覆盖具体错误。
- 最终响应体的 `error.fieldErrors` 包含每个字段的校验失败信息，前端可以直接渲染。

---

## 二、WebFlux 环境下的校验异常处理

Spring WebFlux 是响应式 Web 框架，其异常处理机制与 Servlet 略有不同。项目中提供了 `GlobalWebFluxValidationExceptionHandler`，适配了 `WebExchangeBindException` 和 `ServerWebInputException`。

### 1. 处理 `WebExchangeBindException`

```java
@ExceptionHandler(WebExchangeBindException.class)
public Mono<Result<Void>> handleWebExchangeBindException(WebExchangeBindException ex,
                                                         ServerWebExchange exchange) {
    Map<String, String> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                    FieldError::getField,
                    fieldError -> fieldError.getDefaultMessage() == null ? "无效值" : fieldError.getDefaultMessage(),
                    (msg1, msg2) -> msg1
            ));
    return buildValidationErrorResult(fieldErrors, exchange);
}
```

- 与 `BindException` 类似，但返回值是 `Mono<Result>`，且参数为 `ServerWebExchange`。
- 获取路径使用 `exchange.getRequest().getPath().value()`。

### 2. 处理请求体解析失败

```java
@ExceptionHandler(ServerWebInputException.class)
public Mono<Result<Void>> handleServerWebInputException(ServerWebInputException ex,
                                                        ServerWebExchange exchange) {
    BaseRuntimeException exception = BaseErrorCode.SERVER_ERROR.throwed(ex.getReason());
    return Mono.just(GlobalExceptionHandler.resolveException(exception, exchange.getRequest().getPath().value()));
}
```

- `ServerWebInputException` 常发生在请求体格式错误（如 JSON 解析失败）时，此时无法到达 JSR-303 校验，但同样需要友好反馈。
- 这里将其转为 `SERVER_ERROR` 异常，并传递 `reason` 作为 detail。

### 3. 与 Servlet 版本的区别

| 维度 | Servlet (`GlobalValidationExceptionHandler`) | WebFlux (`GlobalWebFluxValidationExceptionHandler`) |
|------|----------------------------------------------|-----------------------------------------------------|
| 响应类型 | 同步 `Result` | 异步 `Mono<Result>` |
| 异常类 | `MethodArgumentNotValidException`, `BindException`, `ConstraintViolationException` | `WebExchangeBindException`, `ServerWebInputException` |
| 请求对象 | `HttpServletRequest` | `ServerWebExchange` |
| 路径获取 | `request.getRequestURI()` | `exchange.getRequest().getPath().value()` |

尽管底层框架不同，但**业务逻辑高度复用**，特别是字段错误提取和 `resolveException` 工具方法，体现了良好的抽象。

---

## 三、生产环境最佳实践建议

### 1. 错误信息脱敏与国际化

- 生产环境不应返回堆栈信息，可通过配置开关（如 `spring.profiles.active`）控制是否填充 `stackTrace`。
- `Feedback` 中的 `message` 应当支持国际化（使用 `MessageSource`），便于多语言场景。

### 2. 日志分级

- `BaseRuntimeException` 属于**已知业务异常**，记录 `warn` 级别即可，避免日志泛滥。
- 未知异常记录 `error` 级别，并携带完整堆栈，便于运维排查。

### 3. 校验错误的结构化输出

- 将 `fieldErrors` 作为 Map 返回，前端可以直接绑定到表单控件，而不需要解析字符串。
- 若需要错误码与字段对应，可扩展 `fieldErrors` 为对象列表（包含 code、message、field）。

### 4. 全局处理器的优先级

- 细粒度处理器（如 `GlobalValidationExceptionHandler`）与粗粒度处理器（`GlobalExceptionHandler`）应避免重复拦截。通常将 `GlobalExceptionHandler` 作为最后的兜底，而校验处理器通过 `@Order` 指定顺序或直接定义在独立类中，Spring 会按异常类型匹配最精确的处理器。

### 5. WebFlux 中的响应式上下文

- 在 WebFlux 中，`traceId` 的传递可能需要借助 Reactor 上下文（`Context`），而不能仅依赖 MDC（`MDC` 在响应式环境下可能失效）。建议使用 `org.slf4j.MDC` 结合 `contextWrite` 或自定义 Reactor 钩子。

---

## 四、总结

本文通过分析项目中的异常处理代码，展示了如何构建一套**兼容 Servlet 和 WebFlux**、**区分业务与系统异常**、**精细处理校验错误**的统一异常处理方案。核心思想是：

- **单一职责**：每个处理器只负责一类异常。
- **复用工具方法**：`resolveException` 集中处理通用属性填充。
- **错误信息分层**：`Result` 包含基础信息、错误详情和字段级错误。
- **保持响应式与非响应式的一致性**：虽然返回类型不同，但业务逻辑相同，便于开发者理解。

这套设计不仅提升了开发效率，也保证了接口返回格式的统一性和可读性，是构建企业级 Spring Boot 应用的优秀实践。