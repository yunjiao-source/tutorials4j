# [068][公共模块]Spring Boot 全局异常处理与参数校验实战（上）：架构设计与响应封装

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

在微服务和 RESTful API 盛行的今天，一套清晰、统一、可维护的异常处理机制是项目质量的重要保障。本文基于一个真实项目的异常处理模块，深入剖析如何利用 Spring Boot 的 `@RestControllerAdvice` 和 JSR-303 校验框架，打造一套兼顾 Servlet 容器和 WebFlux 响应式环境的全局异常处理方案。

## 一、整体架构设计

异常处理模块由以下核心组件构成：

- **`Result<T>`**：统一的 API 响应体，包含状态码、消息、时间戳、路径、traceId 以及可选的错误详情（`Error` 内部类）。
- **`BaseRuntimeException`**：自定义业务运行时异常，聚合了 `ErrorCode`（错误码枚举）、参数、详情等信息。
- **`GlobalExceptionHandler`**：全局异常处理器，拦截 `BaseRuntimeException` 和通用 `Exception`，将异常转化为标准的 `Result` 响应。
- **`GlobalValidationExceptionHandler`**：专门处理参数校验异常（`MethodArgumentNotValidException`、`BindException`、`ConstraintViolationException`），将字段校验错误转换为 `Result.fieldErrors` 结构。
- **`GlobalWebFluxValidationExceptionHandler`**：WebFlux 环境下的校验异常处理器，处理 `WebExchangeBindException` 和 `ServerWebInputException`，返回 `Mono<Result>`。

这种分层设计将**业务异常**、**系统异常**和**校验异常**区分开，既保证了通用性，又为特定场景提供了精细化处理。

---

## 二、统一响应体 `Result<T>` 的设计亮点

`Result` 类采用链式调用的 Builder 风格，同时包含一个内部 `Error` 类用于承载调试信息：

```java
public class Result<T> {
    private final Instant timestamp = Instant.now();
    private String message;
    private String path;
    private T data;
    private int status;
    private String code;
    private String traceId;
    private Error error;

    // 静态工厂方法
    public static <T> Result<T> success(T data) { ... }
    public static <T> Result<T> failure() { ... }

    // 链式设置方法
    public Result<T> fieldErrors(Map<String, String> fieldErrors) { ... }
    public Result<T> errorDetail(String detail) { ... }
    // ...
}
```

**设计要点**：

1. **时间戳自动生成**：每个响应都带 `timestamp`，便于问题追踪。
2. **traceId 支持**：通过 MDC 传递分布式追踪 ID，与日志链路打通。
3. **错误信息分层**：`message` 为用户友好的提示，`Error` 对象内包含 `detail`（技术细节）、`fieldErrors`（字段校验错误）、`stackTrace`（仅系统异常时返回）和 `params`（异常参数），兼顾了用户体验和开发调试。
4. **状态码与 HTTP 状态分离**：`status` 存储 HTTP 状态码（如 400、500），`code` 为业务错误码（如 "VALIDATION_FAILED"），便于前端根据不同错误码做差异化处理。

---

## 三、自定义异常 `BaseRuntimeException` 与错误码枚举

`BaseRuntimeException` 继承了 `RuntimeException`，内部持有 `ErrorCode` 接口和参数 Map：

```java
@Getter
public class BaseRuntimeException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Map<String, Object> params;
    private final String detail;

    // 构造方法支持链式添加参数
    public BaseRuntimeException param(String key, Object value) { ... }

    public Result<Void> getResult() { ... }
}
```

`ErrorCode` 接口通常由枚举实现（如 `BaseErrorCode`），每个枚举项包含一个 `Feedback` 对象，后者定义了 `code`（业务码）、`httpStatus` 和 `message`。这种设计使得错误码集中管理，且支持国际化扩展。

**关键方法 `getResult()`**：将当前异常转换为 `Result` 对象，若为系统错误且存在 cause，则携带堆栈信息（生产环境可通过配置关闭）。

---

## 四、全局异常处理器 `GlobalExceptionHandler`

该处理器是兜底拦截器，处理所有未捕获的异常：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseRuntimeException.class)
    public Result<Void> handleBaseException(BaseRuntimeException ex, HttpServletRequest request) {
        return resolveException(ex, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleOtherException(Exception ex, HttpServletRequest request) {
        return resolveException(ex, request.getRequestURI());
    }

    public static Result<Void> resolveException(Exception ex, String path) {
        Result<Void> result = Result.failure();
        if (ex instanceof BaseRuntimeException baseRuntimeException) {
            log.warn("业务异常: {}", result);
            result = baseRuntimeException.getResult();
        } else {
            log.error("系统异常", ex);
        }
        result.path(path)
              .errorDetail(ex.getMessage())
              .errorStackTrace(ex.getStackTrace())
              .traceId(MDC.get(DefaultConsts.HTTP_HEADER_TRACE_ID));
        return result;
    }
}
```

**亮点**：

- 使用 `static` 方法 `resolveException`，便于其他处理器复用（如后续的校验异常处理器）。
- 对 `BaseRuntimeException` 仅记录 warn 级别日志，对未知异常记录 error 级别并打印堆栈，**区分了业务预期异常与非预期系统异常**。
- 统一补充 `path`、`traceId` 和堆栈信息，保证响应完整性。

---

## 五、参数校验异常处理的挑战

Spring Boot 中参数校验可能抛出三种常见异常：

| 异常类型 | 触发场景 |
|---------|---------|
| `MethodArgumentNotValidException` | `@RequestBody` 标注的 JSON 请求体验证失败 |
| `BindException` | `@ModelAttribute` 或表单参数绑定验证失败 |
| `ConstraintViolationException` | `@RequestParam`、`@PathVariable` 等单个参数校验失败（需配合 `@Validated`） |

这三类异常都需要将字段校验错误（`FieldError` 或 `ConstraintViolation`）转换为前端友好的键值对，而不是简单的 `message`。为此，项目专门设计了 `GlobalValidationExceptionHandler`。

---

## 六、小结（上篇）

本文介绍了异常处理模块的整体架构、统一响应体设计、自定义异常体系和基础全局处理器。下一篇我们将深入参数校验异常处理器，重点分析 Servlet 环境与 WebFlux 环境下的异同，并给出生产环境的优化建议。
