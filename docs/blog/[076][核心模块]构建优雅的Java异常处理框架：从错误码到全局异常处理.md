# [076][核心模块]构建优雅的Java异常处理框架：从错误码到全局异常处理

本文章代码: https://gitee.com/yunjiao-source/tutorials4j

在现代Web应用开发中，异常处理是保证系统健壮性和可维护性的关键环节。一个设计良好的异常处理框架不仅能统一错误响应格式，还能简化业务代码，提升开发效率。本文将从实际代码出发，深入剖析一套基于Spring Boot的异常处理框架，涵盖错误码设计、异常体系、全局拦截器以及最佳实践，帮助读者构建属于自己的高质量异常处理方案。

---

## 一、框架整体概览

该框架由以下几个核心模块组成：

- **错误码定义**（`ErrorCode`接口 + `BaseErrorCode`枚举）
- **错误反馈**（`Feedback`抽象类及各类HTTP状态反馈子类）
- **异常类**（`BaseException`受检异常 + `BaseRuntimeException`非受检异常）
- **全局异常处理器**（`GlobalExceptionHandler`）
- **统一响应对象**（`Result`，代码未给出但可推断）

其核心设计思想是：**将业务异常与HTTP协议状态解耦，通过错误码枚举统一管理，并提供灵活的异常传播和转换机制**。下面我们将逐层拆解。

---

## 二、错误码与反馈设计

### 2.1 `ErrorCode` 接口：错误码的统一契约

```java
public interface ErrorCode {
    Feedback getFeedback();

    default BaseRuntimeException throwed() {
        return new BaseRuntimeException(this);
    }
    default BaseRuntimeException throwed(String message) { ... }
    default BaseRuntimeException throwed(String message, Throwable cause) { ... }
    default BaseRuntimeException throwed(Throwable cause) { ... }
}
```

`ErrorCode` 接口定义了错误码的核心行为——获取 `Feedback` 对象。同时，它提供了四个 `default` 方法，用于快速抛出 `BaseRuntimeException`。这一设计让枚举常量可以直接“化身”为异常工厂，实现了**错误码即异常源**的语义，极大简化了业务代码中的异常抛出逻辑。

### 2.2 `Feedback` 抽象类：HTTP 状态与消息的载体

```java
@Data
@RequiredArgsConstructor
public abstract class Feedback {
    private final String message;
    private final int httpStatus;
    private String code;   // 由枚举注入

    public boolean isSystemError() {
        return httpStatus >= 500;
    }
}
```

`Feedback` 封装了三个关键属性：
- `message`：面向用户的可读错误描述
- `httpStatus`：对应的HTTP状态码（如200、400、500）
- `code`：内部错误码，实际为枚举常量的名称（如 `VALIDATION_FAILED`）

`isSystemError()` 方法用于区分系统级错误（状态码≥500）和业务级错误，这对后续的日志记录和监控有重要意义。

### 2.3 `BaseErrorCode` 枚举：错误码的集中注册中心

```java
@Getter
public enum BaseErrorCode implements ErrorCode {
    OK(new OkFeedback("成功")),
    NO_CONTENT(new NoContentFeedback("无内容")),
    VALIDATION_FAILED(new BadRequestErrorFeedback("接口参数校验失败")),
    UNAUTHORIZED(new UnauthorizedFeedback("未经授权")),
    // ... 更多枚举
    INTERNAL_SERVER_ERROR(new InternalServerErrorFeedback("服务器内部错误")),
    // 技术异常映射
    NULL_POINTER_EXCEPTION(new InternalServerErrorFeedback("发生了空指针异常")),
    // ...
}
```

枚举常量通过构造器传入对应的 `Feedback` 子类实例，并在构造器中调用 `feedback.setCode(this.name())`，将枚举名作为内部错误码。这样，前端收到的错误响应中将包含一个语义明确的字符串代码（如 `VALIDATION_FAILED`），便于客户端进行针对性处理，而不仅仅是依赖HTTP状态码。

**设计亮点**：
- **状态与消息分离**：每个错误码关联固定的HTTP状态和消息模板，保持一致性。
- **扩展性强**：新增错误码只需添加枚举常量并关联对应的 `Feedback` 子类，无需修改其他逻辑。
- **技术异常抽象化**：将常见的技术异常（`NullPointerException`、`IOException`等）也映射为内部错误码，使其能够统一被框架处理，避免在全局处理器中硬编码异常类型。

---

## 三、异常体系设计

### 3.1 受检异常 `BaseException`

```java
public class BaseException extends Exception {
    // 标准构造函数
}
```

该类目前仅作为空壳，未实际使用。在大多数Web应用中，我们倾向于使用非受检异常（`RuntimeException`）来避免层层`throws`，但保留受检异常可满足特定场景（如需要强制调用方处理的业务异常）。后续可考虑将其改造为携带错误码的受检异常版本，但当前框架主要依赖 `BaseRuntimeException`。

### 3.2 非受检异常 `BaseRuntimeException`

```java
@Getter
public class BaseRuntimeException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Map<String, Object> params;
    private final String detail;
    // ... 构造函数
}
```

该异常是框架的核心异常类型，具备以下特性：

- **携带错误码**：通过 `errorCode` 关联到 `BaseErrorCode` 枚举，保证错误响应的统一性。
- **携带额外参数**：`params` 是一个 `Map`，用于存放需要传递给前端的动态数据（例如校验失败时具体的字段错误）。
- **链式设置参数**：提供 `param(String key, Object value)` 方法返回自身，支持流畅的链式调用。
- **自动构建消息**：构造时通过 `buildMessage` 方法生成包含 `[错误码 | HTTP状态]消息` 格式的异常消息，便于日志追踪。
- **一键生成响应对象**：`getResult()` 方法将当前异常转换为 `Result` 对象，并可根据是否为系统错误决定是否附带堆栈信息。

使用示例：
```java
throw BaseErrorCode.VALIDATION_FAILED
    .throwed("用户名不能为空")
    .param("field", "username");
```

### 3.3 异常到响应的转化

`getResult()` 方法展示了如何将异常信息转化为统一的API响应结构：
```java
public Result<Void> getResult() {
    Result<Void> result = Result.failure(this.getErrorCode());
    result.errorDetail(this.getDetail())
          .errorParams(this.getParams());
    Feedback feedback = this.getErrorCode().getFeedback();
    if (feedback.isSystemError() && this.getCause() != null) {
        result.errorStackTrace(this.getCause().getStackTrace());
    }
    return result;
}
```

- 对于系统级错误，会附带堆栈信息（便于开发调试，生产环境可关闭）。
- `Result` 对象通常包含 `code`、`message`、`status`、`detail`、`params` 等字段，其 `status` 字段应与HTTP状态码一致。

---

## 四、全局异常处理器

### 4.1 处理 `BaseRuntimeException`

```java
@ExceptionHandler(BaseRuntimeException.class)
public Result<Void> handleBaseException(
        BaseRuntimeException ex, HttpServletRequest request, HttpServletResponse response) {
    Result<Void> result = ex.getResult()
            .path(request.getRequestURI())
            .traceId(MDC.get(DefaultConsts.HTTP_HEADER_TRACE_ID));
    if (ex.getErrorCode().getFeedback().isSystemError()) {
        log.error("系统异常", ex);
    } else {
        log.warn("业务异常: {}", result);
    }
    response.setStatus(result.getStatus());
    return result;
}
```

- **获取响应对象**：直接调用异常的 `getResult()` 方法。
- **补充上下文信息**：添加请求路径和链路追踪ID（从MDC获取）。
- **分层日志**：系统级错误记录 `error` 级别，业务级错误记录 `warn` 级别，便于运维监控。
- **设置HTTP状态码**：通过 `response.setStatus` 明确返回给客户端的HTTP状态。

### 4.2 处理其他异常（兜底）

```java
@ExceptionHandler(Exception.class)
public Result<Void> handleOtherException(
        Exception ex, HttpServletRequest request, HttpServletResponse response) {
    ErrorCode initErrorCode = BaseErrorCode.INTERNAL_SERVER_ERROR;
    String className = ex.getClass().getSimpleName();
    if (EXCEPTION_DICTIONARY.containsKey(className)) {
        initErrorCode = EXCEPTION_DICTIONARY.get(className);
    }
    Result<Void> result = resolveException(ex, request.getRequestURI(), initErrorCode);
    response.setStatus(result.getStatus());
    return result;
}
```

- **异常类型映射**：使用 `EXCEPTION_DICTIONARY` 将常见异常类名映射到对应的 `BaseErrorCode`，例如 `NoResourceFoundException` → `RESOURCE_NOT_FOUND`。这是一种简洁的映射策略，避免了大量 `instanceof` 判断。
- **统一解析**：调用 `resolveException` 方法进行进一步处理。

### 4.3 验证异常的处理

`resolveException` 方法针对三种校验异常做了特殊处理：
- `WebExchangeBindException`（Spring WebFlux）
- `ConstraintViolationException`（方法参数校验）
- `BindException`（表单绑定校验）

这些异常会提取字段校验错误信息，并放入 `Result.fieldErrors` 中，使前端能清晰展示每个字段的校验失败原因。

```java
Map<String, String> fieldErrors = bindException.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            fe -> fe.getDefaultMessage() == null ? "无效值" : fe.getDefaultMessage(),
            (a, b) -> a));
result.fieldErrors(fieldErrors);
```

### 4.4 日志与堆栈策略

在 `resolveException` 中：
- 若默认错误码为系统错误（状态码≥500），则记录完整堆栈（`errorStackTrace`）并打印 `error` 日志。
- 否则仅记录 `warn` 日志，不返回堆栈信息，避免泄露内部细节。

---

## 五、使用示例与最佳实践

### 5.1 业务代码中抛出异常

```java
@Service
public class UserService {
    public User findUser(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw BaseErrorCode.RESOURCE_NOT_FOUND
                .throwed("用户不存在，id=" + id)
                .param("id", id);
        }
        return user;
    }
}
```

### 5.2 校验失败场景

若使用了Spring Validation，全局处理器会自动捕获校验异常并填充 `fieldErrors`，业务代码无需额外处理。例如：

```java
@PostMapping("/user")
public Result<Void> createUser(@Valid @RequestBody UserCreateDto dto) {
    // 校验失败会抛出 MethodArgumentNotValidException（BindException的子类）
    // 由全局处理器统一处理
}
```

### 5.3 最佳实践建议

1. **错误码命名规范**：建议使用 `模块_错误类型` 的形式（如 `USER_NOT_FOUND`），便于后期扩展。
2. **避免异常吞噬**：在捕获 `Exception` 时，若未重新抛出，务必记录日志，避免问题被淹没。
3. **参数传递控制**：`params` 字段应只用于传递必要的前端展示数据，避免放入大对象或敏感信息。
4. **区分系统与业务异常**：合理设置HTTP状态码，4xx代表客户端问题，5xx代表服务端问题，便于前端做通用处理。
5. **生产环境堆栈处理**：可在 `getResult()` 中通过环境配置决定是否返回堆栈信息，避免信息泄露。

---

## 六、总结与改进建议

### 6.1 现有框架的优点

- **清晰的分层**：错误码、反馈、异常、处理器各司其职，职责单一。
- **高度可扩展**：新增错误码只需添加枚举，新增异常类型只需在映射字典中注册。
- **良好的日志与监控支持**：通过 `isSystemError` 区分日志级别，并支持链路追踪。
- **链式API**：`throwed().param()` 模式使异常抛出代码简洁易读。

### 6.2 可改进之处

1. **受检异常未使用**：`BaseException` 目前未被利用，可考虑使其也携带错误码，并提供 `throwed` 方法的重载，以满足需要受检异常的场景。
2. **异常映射字典局限性**：`EXCEPTION_DICTIONARY` 依赖类名字符串，若类名重命名则需同步修改，且不支持继承类匹配。可考虑使用 `Class` 对象或引入 `Predicate` 进行更灵活的条件匹配。
3. **国际化支持**：消息字符串目前硬编码在 `Feedback` 中，若需多语言支持，可改为消息键，结合 `MessageSource` 动态解析。
4. **参数校验错误消息可定制**：目前校验错误消息直接使用注解中的 `message`，可进一步支持通过错误码统一管理校验消息。

### 6.3 结语

本文通过对一套实战级异常处理框架的源码解读，展示了如何将错误码、异常、反馈和全局处理有机整合，形成一个既规范又灵活的体系。这套设计能够显著提升团队协作效率、降低维护成本，并保证最终用户获得一致且友好的错误反馈。读者可根据自身业务需求加以借鉴和改造，构建出适合自己项目的异常处理“利器”。

