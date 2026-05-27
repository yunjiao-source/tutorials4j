该代码展示了一个 Spring Boot 应用中三种不同的资源加载方式，用于读取 `classpath:resourceload/config.properties` 配置文件，并输出其内容。

### 核心功能
- **资源加载演示**：通过 `@Value`、`ResourceLoader`、`ResourcePatternResolver` 三种机制获取 Spring 的 `Resource` 对象。
- **内容读取与输出**：利用 `Utils.print()` 方法读取资源内容（以字符串和 `Properties` 对象两种形式），并通过日志打印。

### 三种加载方式对比

| 方式 | 实现类 | 特点 | 适用场景 |
|------|--------|------|----------|
| ① `@Value` + `Resource` | `ValueDemoService` | 编译期确定路径，直接注入 `Resource` | 固定、少量的配置文件或模板 |
| ② `ResourceLoader` | `ResourceLoaderDemoService` | 运行期动态拼接路径，通过 `getResource()` 加载 | 路径依赖外部配置或用户输入 |
| ③ `ResourcePatternResolver` | `ResourcePatternResolverDemoService` | 支持通配符（`*.properties`），批量加载多个资源 | 需要一次性读取符合命名规则的所有文件 |

### 辅助组件
- **`RunnerConfig`**：注册三个 `CommandLineRunner`，应用启动后依次执行各服务的 `print()` 方法，演示效果。
- **`Utils`**：工具类，检查资源存在性，读取内容并转换为字符串和 `Properties`，统一输出日志。

### 预期执行流程
1. 应用启动。
2. 先后执行 `ValueDemoService`、`ResourceLoaderDemoService`、`ResourcePatternResolverDemoService`。
3. 每个服务会读取 `classpath:resourceload/config.properties`（或其他匹配的 properties 文件），控制台输出类似：
   ```
   >>> 使用 ValueDemoService：
   配置文件内容：key=value
   配置文件内容：{key=value}
   ```

该代码主要用于教学或技术验证，帮助理解 Spring 中 `Resource` 抽象及不同加载器的用法区别。