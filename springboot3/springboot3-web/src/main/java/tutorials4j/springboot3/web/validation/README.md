这些代码展示了在 **Spring Boot 3** 中如何使用 Jakarta Validation（`jakarta.validation`）对请求参数（包括 `@RequestBody`、`@RequestParam`、`@PathVariable`）进行校验，并提供了一个全局异常处理器来统一错误响应的格式。

## 1. 核心校验实体与包装类

### `User.java`
- 定义了一个简单的实体类，其中 `age` 字段使用 `@Min(10)` 注解，要求年龄必须 **大于等于 10**。
- 使用 Lombok `@Data` 简化代码。

### `UserClass.java`
- 包装了一个 `User` 对象，并额外包含 `className` 字段。
- 在 `user` 字段上标记了 `@Valid`，表示对嵌套对象 `User` 也会进行级联校验。

### `ValidList.java`
- 自定义实现了 `List<E>` 接口的包装类，内部维护一个 `List<E>`。
- 关键点：内部列表使用了 `@Valid` 注解。这样当在控制器方法中使用 `@RequestBody @Valid ValidList<User> users` 时，可以逐个校验列表中的每个 `User` 对象。
- Spring 默认对 `List` 直接使用 `@Valid` 不会进行元素校验（因为泛型擦除），此包装类解决了集合元素校验的问题。

## 2. 控制器示例

### `ValidController.java`
- 提供两个 POST 接口：
    - `/valid/check`：直接接收 `@RequestBody @Valid User`，校验单个用户。
    - `/valid/check-multilevel`：接收 `@RequestBody @Valid UserClass`，触发对 `User` 的级联校验。
- 接口返回接收到的对象（校验通过时）。

### `ValiddatedController.java`
- 该类使用了 `//@Validated`（被注释掉），注释中说明：若类上不加 `@Validated`，则 `@RequestParam` 和 `@PathVariable` 上的校验注解 **不会生效**，并且会抛出 `HandlerMethodValidationException`；若加上则会抛出 `ConstraintViolationException`。全局处理器同时处理了这两种异常。
- 提供接口：
    - `POST /validated/check-list`：接收 `@RequestBody @Valid List<User>`。**注意：这里直接对 `List<User>` 使用 `@Valid` 实际上不会校验列表内元素**（因为泛型擦除），这可能是演示一个“错误”用法。
    - `POST /validated/check-valid-list`：接收 `@RequestBody @Valid ValidList<User>`，会对列表中每个 `User` 进行校验。
    - `GET /validated/check-param`：接收 `@RequestParam age`，校验 `@Max(99)`。
    - `GET /validated/check-path/{id}`：接收路径参数 `id`，校验 `@Pattern(regexp = "^[0-9]*$")`（必须是数字字符串）。

## 3. 全局异常处理器

### `ValidResponseEntityExceptionHandler.java`
- 继承自 `ResponseEntityExceptionHandler`，并添加 `@RestControllerAdvice`。
- 重写 / 新增了以下处理方法：

#### `handleHandlerMethodValidationException`
- 处理 `HandlerMethodValidationException`，这类异常通常发生在方法参数（如 `@RequestParam`、`@PathVariable`）校验失败且类上**没有** `@Validated` 注解时。
- 将错误信息提取后放入 `ProblemDetail` 的 `errors` 属性中，返回格式类似：
  ```json
  {
    "type": "about:blank",
    "title": "方法校验异常",
    "status": 400,
    "errors": ["不能大于99岁"]
  }
  ```

#### `handleMethodArgumentNotValid`
- 重写父类方法，处理 `@RequestBody` 参数校验失败（触发 `MethodArgumentNotValidException`）。
- 将字段错误（field + 错误消息）放入 `ProblemDetail` 的 `errors` 属性中，返回格式如：
  ```json
  {
    "errors": {
      "age": "年龄必须大于10岁"
    }
  }
  ```

#### `handleConstraintViolationException`
- 通过 `@ExceptionHandler` 处理 `ConstraintViolationException`，这类异常通常在**类上有 `@Validated` 注解**时，对 `@RequestParam`、`@PathVariable` 校验失败时抛出。
- 同样将违规信息（属性路径 + 消息）转换为 Map 放入 `ProblemDetail` 的 `errors` 属性中。

## 4. 整体功能总结

这套代码实现了一个完整的 **Spring Boot 3 请求参数校验与统一错误响应** 方案：

| 场景 | 使用的注解 | 触发的异常 | 处理器 |
|------|------------|------------|--------|
| `@RequestBody` 单对象 | `@Valid` | `MethodArgumentNotValidException` | `handleMethodArgumentNotValid` |
| `@RequestBody` 嵌套对象 | `@Valid` + 嵌套字段 `@Valid` | 同上 | 同上 |
| `@RequestBody` 集合元素校验 | `ValidList` 包装类 + `@Valid` | 同上 | 同上 |
| `@RequestParam` / `@PathVariable`（无 `@Validated`） | `@Max`、`@Pattern` 等 | `HandlerMethodValidationException` | `handleHandlerMethodValidationException` |
| `@RequestParam` / `@PathVariable`（有 `@Validated`） | 同上 | `ConstraintViolationException` | `handleConstraintViolationException` |

所有校验失败都会返回 **HTTP 400**，响应体为 `ProblemDetail` 格式，并且在 `errors` 字段中包含了详细的字段级错误信息，便于客户端展示。

## 5. 关键设计点

- **解决集合校验**：通过自定义 `ValidList` 包装类，让 `@Valid` 能够对集合内元素递归校验。
- **兼容两种参数校验异常**：Spring 3 中对普通方法参数（非 `@RequestBody`）校验失败会抛出 `HandlerMethodValidationException`（类上无 `@Validated` 时）或 `ConstraintViolationException`（类上有 `@Validated` 时）。全局处理器同时覆盖了两者。
- **统一响应结构**：所有校验异常最终都包装为 `ProblemDetail`，并且额外附加 `errors` 字段，保持了 API 的一致性。

> 注意：代码中的 `ValiddatedController` 类名拼写有误（多了一个 `d`），但这不影响功能。另外，`//@Validated` 被注释掉，意味着该控制器中 `@RequestParam` 和 `@PathVariable` 的校验实际上不会生效（除非手动取消注释）。