# [024][Web模块]基于 AntiSamy 的 Spring Boot XSS 防护实践：从过滤器到反序列化的多层防御

本项目代码:https://gitee.com/yunjiao-source/tutorials4j

## 摘要

跨站脚本攻击（XSS）一直是 Web 应用最常见的安全漏洞之一。本文基于一个真实的企业级 Java 项目，深入分析了一套完整的 XSS 防护方案。该方案使用 OWASP AntiSamy 作为核心清洗引擎，通过 **Servlet 过滤器** 和 **Jackson 反序列化器** 两层机制，分别拦截请求参数与 JSON 正文中的恶意代码，实现了对用户输入的全方位防护。文章将详细剖析 `XssHttpServletFilter`、`XssJsonDeserializer`、`XssSimpleModule` 以及 `XssUtils` 四个核心组件的设计与实现，并给出在 Spring Boot 中的集成示例。

## 1. 引言

XSS 攻击的本质是攻击者将恶意脚本注入到看似可信的网站中，当其他用户浏览时，脚本会在其浏览器中执行。传统的防御手段包括输出转义、输入校验、内容安全策略（CSP）等。其中，**输入清洗**（即过滤掉 HTML/JS 中的危险内容）是一种广谱且有效的防御手段。

本文介绍的方案基于 OWASP 的 AntiSamy 项目——一个专门用于过滤用户提交 HTML/CSS 内容的 Java 库。开发者通过提供一个策略文件（XML），精细控制哪些标签、属性、CSS 属性是允许的。与简单的正则替换相比，AntiSamy 能正确解析 HTML 结构，避免绕过攻击。


## 2. 整体架构

下图展示了 XSS 防护的整体数据流：

```text
HTTP Request (GET/POST)
       │
       ▼
┌──────────────────────┐
│ XssHttpServletFilter │  → 包装 HttpServletRequest
└──────────────────────┘
       │
       ▼ (表单参数、Query参数、请求头)
┌──────────────────────────────┐
│ XssHttpServletRequestWrapper │  → 重写 getParameter 等方法
└──────────────────────────────┘
       │
       ▼
       XssUtils.cleaning()  ← AntiSamy 清洗
       │
       ▼
Controller 接收到的参数已清洗

────────────────────────────

HTTP Request (JSON Body)
       │
       ▼
┌─────────────────────┐
│ XssSimpleModule     │  → 注册到 Jackson ObjectMapper
└─────────────────────┘
       │
       ▼
┌─────────────────────────┐
│ XssJsonDeserializer     │  → 反序列化 String 字段时调用
└─────────────────────────┘
       │
       ▼
       XssUtils.cleaning()
       │
       ▼
@RequestBody 对象中的字符串已清洗
```

### 关键设计思想

1. **非侵入式**：过滤器与 Jackson 模块均通过配置加入，业务代码零改动。
2. **分层防御**：同时覆盖 `application/x-www-form-urlencoded`、`multipart/form-data`、`query string` 以及 `application/json` 四种常见请求格式。
3. **可配置策略**：通过修改 AntiSamy 策略文件，可灵活控制允许的标签（如保留 `<b>`、`<i>` 而删除 `<script>`）。

## 3. 核心组件详解

### 3.1 XssUtils – 底层清洗引擎

`XssUtils` 是防 XSS 的核心工具类，封装了 AntiSamy 的调用逻辑。采用**单例模式**，确保策略文件只加载一次。

```java
private XssUtils() {
    Policy policy = createPolicy();
    this.antiSamy = ObjectUtils.isNotEmpty(policy) ? new AntiSamy(policy) : new AntiSamy();
    this.nbsp = cleanHtml("&nbsp;");
    this.quot = cleanHtml("\"");
}
```

**关键特性**：

- **策略加载**：从 classpath `antisamy/antisamy-anythinggoes.xml` 读取策略文件。若加载失败（例如文件缺失），则回退到 AntiSamy 的默认策略。
- **字符乱码修复**：AntiSamy 在处理 `&nbsp;` 和双引号时可能产生异常字符（取决于策略和 JDK 版本）。代码预先清洗这两个字符串，然后在最终结果中用 `replaceAll` 将其还原或清除。
- **实体反转义**：调用 `StringEscapeUtils.unescapeHtml4()` 对输入先进行反转义，避免 AntiSamy 二次转义导致误判。

**清洗流程**（`cleaning` 方法）：

1. 反转义 HTML 实体（如 `&lt;` → `<`）。
2. 调用 `cleanHtml` → `antiSamy.scan()`。
3. 替换 `&nbsp;` 产生的乱码为空字符串。
4. 替换双引号乱码为原始双引号。
5. 移除所有换行符（可根据需求调整）。
6. 返回清洗后的字符串。

> **注意**：移除换行符可能影响多行文本输入，实际项目中可移除此步骤或改为可选配置。

### 3.2 XssHttpServletFilter – 请求参数清洗

该过滤器实现了 `javax.servlet.Filter`，作用是将原始的 `HttpServletRequest` 包装成自定义的 `XssHttpServletRequestWrapper`。

```java
public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(request);
    filterChain.doFilter(xssRequest, servletResponse);
}
```

`XssHttpServletRequestWrapper`会重写以下方法：

- `getParameter(String name)`
- `getParameterValues(String name)`
- `getParameterMap()`
- `getHeader(String name)`

在这些方法中，调用 `XssUtils.cleaning()` 对每个字符串值进行清洗，然后返回安全版本。这样一来，`@RequestParam`、`@ModelAttribute` 甚至原生 `request.getParameter()` 获取到的数据都是已过滤的。

**优点**：一次配置，所有 GET/POST 表单参数自动受到保护。  
**缺点**：可能误伤正常的 HTML 提交（如富文本编辑器内容），需要根据业务场景调整策略文件或提供绕过方式。

### 3.3 XssJsonDeserializer – JSON 字符串清洗

当请求体为 JSON 时，Spring 默认使用 Jackson 进行反序列化。`XssJsonDeserializer` 继承自 `JsonDeserializer<String>`，并注册到 `SimpleModule` 中。

```java
public class XssJsonDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (StringUtils.isNotBlank(value)) {
            return XssUtils.cleaning(value);
        }
        return value;
    }
}
```

任何被 Jackson 反序列化为 `String` 的 JSON 字段都会触发该反序列化器，从而执行 `XssUtils.cleaning()`。

### 3.4 XssSimpleModule – Jackson 模块注册

`XssSimpleModule` 是一个 `SimpleModule` 子类，在构造器中绑定 `String.class` 到 `XssJsonDeserializer`。

```java
public XssSimpleModule() {
    super(XssSimpleModule.class.getName(), JsonConsts.JSON_VERSION);
    this.addDeserializer(String.class, XssJsonDeserializer.instance);
}
```

应用启动时，只需将该模块添加到 `ObjectMapper` 中，即可全局生效。

## 4. 在 Spring Boot 中集成

### 4.1 添加依赖

```xml
<!-- OWASP AntiSamy -->
<dependency>
    <groupId>org.owasp.antisamy</groupId>
    <artifactId>antisamy</artifactId>
    <version>1.7.4</version>
</dependency>
<!-- Apache Commons Text (用于转义/反转义) -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-text</artifactId>
    <version>1.11.0</version>
</dependency>
<!-- Spring Boot Web Starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 4.2 注册 Filter

```java
@Configuration
public class XssFilterConfig {
    @Bean
    public FilterRegistrationBean<XssHttpServletFilter> xssFilter() {
        FilterRegistrationBean<XssHttpServletFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new XssHttpServletFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
```

### 4.3 注册 Jackson Module

```java
@Configuration
public class XssJacksonConfig {
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper mapper = builder.build();
        mapper.registerModule(new XssSimpleModule());
        return mapper;
    }
}
```

> **Spring Boot 自动配置覆盖**：若项目中已有自定义 `ObjectMapper`，请确保本模块被注入；也可通过 `@Primary` 或 `Jackson2ObjectMapperBuilderCustomizer` 添加模块。

### 4.4 策略文件放置

将 `antisamy-anythinggoes.xml` 存放在 `src/main/resources/antisamy/` 目录下。该策略文件可从 OWASP 官方获取或自定义。

## 5. 测试样例

### 5.1 表单/URL 参数测试

**Controller**：

```java
@PostMapping("/comment")
public String addComment(@RequestParam String content) {
    return "Your comment: " + content;
}
```

**请求**：

```http
POST /comment?content=<script>alert(1)</script>Hello
```

**响应**：

```text
Your comment: Hello
```

`<script>` 标签被策略删除。

### 5.2 JSON 请求体测试

**Controller**：

```java
@PostMapping("/comment/json")
public Comment addCommentJson(@RequestBody Comment comment) {
    return comment;
}

public class Comment {
    private String author;
    private String text;
    // getters/setters
}
```

**请求**：

```json
{
    "author": "<img src=x onerror=alert('xss')>John",
    "text": "<b>Safe</b> &lt;script&gt;"
}
```

**响应**（清洗后）：

```json
{
    "author": "John",
    "text": "<b>Safe</b> &lt;script&gt;"
}
```

注意：`&lt;script&gt;` 会被反转义为 `<script>`，然后策略可能删除 `<script>` 标签但保留文本 `"<script>"`（取决于策略配置）。上述显示仅为示意，实际行为跟随策略。

## 6. 注意事项与最佳实践

### 6.1 性能考虑

- AntiSamy 扫描涉及 HTML 解析，对于长文本或高并发场景可能成为性能瓶颈。可考虑缓存清洗结果（基于输入内容的哈希），或仅对包含 `<>`、`&` 等危险字符的输入进行清洗。
- `XssUtils` 是单例，`AntiSamy` 实例是线程安全的，但 `Policy` 对象也应重用（本代码已满足）。

### 6.2 富文本编辑器支持

若要允许用户提交 HTML（如博客正文），不应使用 `XssHttpServletFilter` 无差别清洗，而应仅在输出时转义，或使用白名单策略保留格式化标签。本方案中的 AntiSamy 策略可以配置为只删除恶意标签（如 `<script>`、`onerror` 等），保留 `<b>`、`<i>`、`<p>` 等安全标签，因此适用于富文本场景。

### 6.3 绕过与白名单

绝对的安全是不存在的。AntiSamy 也会存在绕过漏洞，需定期更新策略库和 AntiSamy 版本。建议结合 **CSP（Content Security Policy）** 和 **输出转义** 形成纵深防御。

### 6.4 移除换行符的影响

原始代码中 `result.replaceAll("\n", "")` 会删除所有换行，可能导致多行文本输入（如文本框、文本域）变成一行。建议移除该行或改为可选开关。

### 6.5 特殊字符的二次转义

`StringEscapeUtils.unescapeHtml4()` 会将 `&amp;` 还原为 `&`，这可能改变原始数据。例如用户输入 `&lt;` 本意是展示文本 `<`，反转义后变为 `<`，再经过 AntiSamy 可能会被当作标签处理。通常这种场景很少，但若业务需要保留字面量，可以省略反转义步骤，直接让 AntiSamy 处理原始输入。

## 7. 总结

本文围绕四个核心类，详细介绍了如何在 Spring Boot 应用中构建多层次的 XSS 防御体系：

- `XssUtils`：基于 AntiSamy 的清洗核心，处理所有清洗逻辑。
- `XssHttpServletFilter`：清洗 GET/POST 表单参数及请求头。
- `XssJsonDeserializer` + `XssSimpleModule`：清洗 JSON 请求体中的字符串字段。

该方案已经在生产环境中得到验证，能够有效阻断常见的反射型、存储型 XSS 攻击，同时保留了合理的 HTML 标签（依策略而定）。开发者可根据自身业务需求调整策略文件，以达到安全与功能的最佳平衡。

**扩展建议**：未来可增加对 `@RequestPart` 上传文件内容的清洗，或提供注解 `@NotXss` 以允许个别字段绕过清洗。