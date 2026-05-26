# http-exchange

在 Spring Boot 3.x 中，`HttpExchangeRepository` 是 Actuator 模块下的一个核心接口，用于记录和存储每个 HTTP 请求与响应的完整信息。通过它，开发者可以方便地查看应用收到了哪些请求、返回了什么结果，这对开发调试和问题定位非常有帮助。

### 👑 为何要用 HttpExchangeRepository？

`HttpExchangeRepository` 就像一个内置的“黑匣子”，无需第三方工具就能帮你收集所有 HTTP 请求和响应的详细信息，并提供一个 Actuator 端点供你随时查看。与一般日志相比，它更像一个结构化的“请求-响应”记录盒，信息更完整，查询更便捷。

它主要适用于以下场景：
*   **开发调试**：在不修改业务代码的前提下，快速查看请求参数、响应状态、请求耗时等，让调试过程更加直观。
*   **集成测试**：测试时，可以方便地回溯请求和响应，精确判断接口行为是否符合预期，便于定位问题。
*   **流量分析**：在开发或预发环境，它可以帮助分析请求频率、接口性能等基础聚合信息。

### 🚀 快速上手：开启请求记录功能

开启该功能非常简单，主要有以下步骤。

1.  **添加依赖**
    首先，需在项目的 `pom.xml` 中引入 `spring-boot-starter-actuator` 起步依赖。

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    ```

2.  **配置并暴露 `HttpExchangeRepository` Bean**
    Spring Boot 默认提供了一个基于内存的实现 `InMemoryHttpExchangeRepository`。在你的配置类中声明一个它的 Bean 即可启用记录功能。

    ```java
    import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
    import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;

    @Configuration
    public class ActuatorConfig {

        @Bean
        public HttpExchangeRepository httpExchangeRepository() {
            InMemoryHttpExchangeRepository repository = new InMemoryHttpExchangeRepository();
            // 可选：修改内存中存储的记录条数，默认为100
            repository.setCapacity(1000); 
            return repository;
        }
    }
    ```

3.  **暴露 `httpexchanges` Actuator 端点**
    在 `application.properties` 或 `application.yml` 中配置，以 HTTP 方式暴露 `httpexchanges` 端点。默认情况下，Actuator 端点**仅暴露 `health`**。请务必添加以下配置：

    ```properties
    # yml 版本
    management:
      endpoints:
        web:
          exposure:
            include: httpexchanges,health
          # base-path: /manage # 可选：自定义 Actuator 基础路径
    ```

4.  **验证功能**
    完成上述配置并启动应用后，访问 `http://localhost:8080/actuator/httpexchanges` 即可查看到最新的 HTTP 请求-响应交换记录列表。请注意，`/actuator` 是默认的端点基础路径。

### ⚙️ 进阶玩法：自定义实现

**`InMemoryHttpExchangeRepository`** 会将信息存储在内存中，并提供 `httpexchanges` 端点供查询。但它有容量上限，应用重启后记录会丢失，因此**不建议在生产环境使用**。

对于生产环境，有两种推荐方案：

#### 1. 接入专业的可观测性系统（推荐）
这是更通用的生产级解决方案。Spring Boot 官方建议在生产环境中使用 **Zipkin** 或 **OpenTelemetry** 等专用工具来收集和追踪请求数据。这些系统功能更强大，支持数据持久化和分布式追踪。

#### 2. 实现自定义的 `HttpExchangeRepository`
如果你确实需要将数据存入外部数据库（如 MySQL），可以实现 `HttpExchangeRepository` 接口。接口主要包含两个方法：

*   `void add(HttpExchange httpExchange)`：当有新的请求-响应完成时，此方法会被自动调用，你需要在此实现存储逻辑。
*   `List<HttpExchange> findAll()`：当调用 `/httpexchanges` 端点时，此方法被调用，你需要返回存储的所有记录列表。这也意味着你可以完全自定义 `/httpexchanges` 返回的数据。

下面是使用 Spring Data JPA 将请求记录存储到数据库的示例：

*   **1. 定义 JPA 实体类 (`HttpExchangeRecord`)**

```java
import jakarta.persistence.*;
import org.springframework.boot.actuate.web.exchanges.HttpExchange;

@Entity
public class HttpExchangeRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String method;
    private String uri;
    private int status;
    private long timestamp;

    // 提供一个从 HttpExchange 构建实体的静态方法
    public static HttpExchangeRecord from(HttpExchange exchange) {
        HttpExchangeRecord record = new HttpExchangeRecord();
        record.setMethod(exchange.getRequest().getMethod());
        record.setUri(exchange.getRequest().getUri().toString());
        record.setStatus(exchange.getResponse().getStatus());
        // Spring Boot 3.4+ 可以直接获取时间戳，请注意版本差异
        // record.setTimestamp(exchange.getTimestamp().toEpochMilli());
        return record;
    }
    // ... getters, setters
}
```

**2. 定义 JPA Repository 接口**

```java
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaHttpExchangeRepository extends JpaRepository<HttpExchangeRecord, Long> {
}
```

**3. 实现 `HttpExchangeRepository` 接口，注入并使用 JPA Repository**

```java
import org.springframework.boot.actuate.web.exchanges.HttpExchange;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomDatabaseRepository implements HttpExchangeRepository {
    private final JpaHttpExchangeRepository jpaRepository;

    public CustomDatabaseRepository(JpaHttpExchangeRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void add(HttpExchange httpExchange) {
        // 将 HttpExchange 对象转换为实体并保存
        HttpExchangeRecord record = HttpExchangeRecord.from(httpExchange);
        jpaRepository.save(record);
    }

    @Override
    public List<HttpExchange> findAll() {
        // 从数据库读取所有记录，并转换回 HttpExchange 列表返回
        return jpaRepository.findAll().stream()
                .map(record -> /* 将 record 转换回 HttpExchange */)
                .collect(Collectors.toList());
    }
}
```

### 💡 开发小贴士

1.  **关键版本提示**：Spring Boot 3.0 中，`/httptrace` 端点已被重命名为 `/httpexchanges`。如果你的项目基于 Spring Boot 3.x，请使用本文提到的 `httpexchanges` 端点。
2.  **`management.httpexchanges.recording` 配置**：你可以在 `application.properties` 中调整一些全局设置。
    *   `management.httpexchanges.recording.enabled`: 是否启用 HTTP 交换记录，默认为 `true`。
    *   `management.httpexchanges.recording.include`: 自定义要包含在记录中的信息，例如 `request-headers`, `response-headers`, `cookie` 等。
3.  **优雅关闭**：记得在应用关闭时，如果有必要，可以优雅地关闭数据源连接。
4.  **性能影响**：在高并发场景下，请注意数据存储的压力。即便是内存存储，额外的存储操作也会对性能产生影响，请尽量避免在核心高频接口上过度记录。

`HttpExchangeRepository` 是 Spring Boot Actuator 提供的强大内置工具，它让我们能以极低的成本获得对应用内 HTTP 请求的可观测性。无论是开发调试还是问题排查，它都能成为你的得力帮手。