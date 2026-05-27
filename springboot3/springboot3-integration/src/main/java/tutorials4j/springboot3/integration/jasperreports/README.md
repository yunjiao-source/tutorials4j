该代码实现了一个基于 Spring Boot 和 JasperReports 的报表导出服务，主要功能如下：

### 1. 数据模型 (`User.java`)
- 使用 Lombok `@Data` 注解，包含 `id`、`name`、`age`、`email`、`address` 五个属性。
- 提供全参构造方法，用于创建用户实例。

### 2. 报表生成工具类 (`ReportGenerator.java`)
- **模板加载与编译**：从 classpath 下的 `jasperreports/mybatisUser.jrxml` 加载报表模板，并编译为 `JasperReport` 对象。
- **数据填充**：将 `List<User>` 转换为 `JRBeanCollectionDataSource` 作为数据源，同时设置参数 `title`（值为“用户列表”），填充生成 `JasperPrint`。
- **格式导出**：支持 `pdf`、`xml`、`html` 三种格式。
    - `pdf`：直接导出为 PDF 字节数组。
    - `xml`：导出 XML 字符串并转为字节数组。
    - `html`：先导出到临时 HTML 文件，读取全部字节后删除临时文件（确保资源清理）。
- 不支持的格式抛出 `IllegalArgumentException`。

### 3. REST 控制器 (`ReportController.java`)
- **端点**：`GET /cursorUsers/export/{format}`，其中 `{format}` 为 `pdf`、`xml` 或 `html`。
- **模拟数据**：使用 `Faker` 库生成 10 条随机用户数据（身份证、姓名、年龄、邮箱、地址）。
- **报表生成**：调用 `ReportGenerator.generate(cursorUsers, format)` 获取报表字节内容。
- **响应构建**：
    - `Content-Type`：`application/octet-stream`
    - `Content-Disposition`：`attachment; filename="mybatisUser-report.{format}"`，强制浏览器下载。
    - 返回 `ByteArrayResource` 包装的字节数组。

### 整体流程
1. 客户端请求 `/cursorUsers/export/pdf`（或 xml/html）。
2. 服务端生成随机用户列表。
3. 根据模板和数据生成指定格式的报表。
4. 以附件形式返回报表文件。

### 注意事项
- 报表模板文件 `jasperreports/mybatisUser.jrxml` 必须存在于 classpath 中，且模板内字段需与 `User` 类的属性名匹配（如 `id`、`name` 等）。
- 当前数据为模拟，实际应用中应替换为真实业务数据。
- HTML 导出采用临时文件方式，需确保应用有临时目录的读写权限。