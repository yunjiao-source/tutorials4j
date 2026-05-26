# ftp

该代码实现了一个基于 Spring Boot 3 的 FTP 文件上传服务，主要功能如下：

1. **配置管理**  
   `FTPConfig` 类通过 `@ConfigurationProperties(prefix = "ftp")` 绑定 `application.yml` 中的 FTP 连接参数（服务器地址、端口、用户名、密码），并由 Spring 管理。

2. **REST 接口**  
   `FTPController` 提供 `POST /upload-file` 接口，接收 `multipart/form-data` 格式的文件（参数名 `file`），调用服务层上传文件。

3. **FTP 上传逻辑**  
   `FTPService` 使用 Apache Commons Net 的 `FTPClient`：
    - 连接 FTP 服务器并登录
    - 开启被动模式（`enterLocalPassiveMode()`）
    - 设置传输类型为二进制（`FTP.BINARY_FILE_TYPE`）
    - 将 `MultipartFile` 的输入流通过 `storeFile()` 上传到指定远程路径（硬编码为 `/uploads/` + 原始文件名）
    - 无论成功或异常，最终都会断开连接

4. **主应用类**  
   标准的 Spring Boot 启动类。

**潜在改进点**：
- 未检查登录是否成功（`login()` 返回布尔值）
- 未处理远程目录不存在的情况（可能导致上传失败）
- 未配置连接/超时参数
- 硬编码远程路径 `/uploads/`，可改为可配置
- 文件重名时会直接覆盖，未提供策略选择
- 错误信息直接抛出 `IOException`，可自定义业务异常

整体上，这是一个可运行的、简单的 FTP 文件上传示例。

