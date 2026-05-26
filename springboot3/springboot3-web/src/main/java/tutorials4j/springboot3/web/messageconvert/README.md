# message-convert

这段代码实现了一个**自定义的Spring Web类型转换功能**，允许在控制器中直接将特定格式的字符串（如 `"1,张三"`）自动转换为 `User` 对象。核心是利用 `AnnotationFormatterFactory` 配合自定义注解 `@UserFormat`，通过 `WebMvcConfigurer` 注册到 Spring 格式化系统中。

### 主要组件与职责

| 类/接口 | 作用 |
|--------|------|
| `User` | 简单的实体类，包含 `id`（Long）和 `name`（String）。 |
| `UserFormat` | 自定义注解，用于标记需要被格式化的参数或字段。 |
| `StringToUserFormatter` | 实现 `AnnotationFormatterFactory<UserFormat>`：<br> - `getFieldTypes()` 声明支持 `User.class` 类型。<br> - `getParser()` 定义解析逻辑：按逗号分割字符串，创建并填充 `User` 对象。<br> - `getPrinter()` 定义反向输出（调用 `toString()`）。 |
| `WebDataTypeConfig` | 配置类，实现 `WebMvcConfigurer`，将 `StringToUserFormatter` 注册到 `FormatterRegistry`，使其对带有 `@UserFormat` 的字段/参数生效。 |
| `UserController` | 演示四种使用场景：<br> - `GET /user`：直接使用 `@UserFormat` 标注的方法参数。<br> - `POST /user`：与 `@RequestParam` 结合，接收表单参数。<br> - `GET /user-wrapper`：`UserWrapper` 中的 `user` 字段带有 `@UserFormat`，自动转换。<br> - `POST /user-wrapper`：使用 `@ModelAttribute` 绑定，同样支持字段级注解。 |
| `UserWrapper` | 包装类，包含带 `@UserFormat` 的 `user` 字段和一个 `age` 字段。 |

### 工作流程

1. 客户端发送请求，其中 `user` 参数（或表单字段）值为 `"1,张三"`。
2. Spring MVC 发现该参数或字段带有 `@UserFormat` 注解，且类型为 `User`。
3. 调用注册的 `StringToUserFormatter.getParser()` 返回的解析器。
4. 解析器执行：`text.split(",")` → `user.setId(1L)`、`user.setName("张三")`。
5. 控制器方法接收已填充的 `User` 对象，并返回（响应中会转为 JSON 等格式）。

### 关键设计点

- **注解驱动**：通过 `@UserFormat` 精确控制哪些参数需要转换，避免全局格式化冲突。
- **复用性**：`UserWrapper` 中的字段同样支持，无需额外代码。
- **双向性**：虽然主要使用解析（请求→对象），也提供了打印（对象→响应）能力，保持对称性。

### 总结

该代码实现了一个**可复用的、声明式的字符串到对象转换方案**，避免了在控制器中手动编写 `split` 和 `set` 逻辑，提升了代码的简洁性和可维护性。适用于自定义格式的输入数据绑定场景。