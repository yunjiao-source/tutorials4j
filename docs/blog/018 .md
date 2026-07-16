# [018][web模块]基于AntiSamy的XSS攻击防护过滤器设计与实现

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework

## 一、引言

跨站脚本攻击（XSS）仍是当今Web应用面临的最常见安全威胁之一。攻击者通过注入恶意脚本，可以窃取用户Cookie、会话令牌甚至执行任意操作。对于Java Web应用，常见的防御手段包括对输入输出进行HTML转义、使用内容安全策略（CSP）或引入专门的过滤库。

本文介绍一套基于 **OWASP AntiSamy** 实现的XSS防护过滤器，它能够对HTTP请求参数、请求头进行深度清洗，移除不符合策略的HTML标签、属性和脚本。整套代码分为三个核心类，分别为 `XssHttpServletFilter`、`XssHttpServletRequestWrapper` 和 `XssUtils`，结构清晰、可插拔，适合集成到任何基于Servlet规范的Java Web应用中。

## 二、整体架构与组件职责

| 类名 | 类型 | 职责 |
|------|------|------|
| `XssHttpServletFilter` | `Filter` | 拦截请求，将原始 `HttpServletRequest` 包装为 XSS 安全包装器。 |
| `XssHttpServletRequestWrapper` | `HttpServletRequestWrapper` | 重写 `getParameter`、`getParameterValues`、`getParameterMap`、`getHeader` 等方法，所有获取的数据都经由 `XssUtils.cleaning()` 清洗。 |
| `XssUtils` | 工具类（单例） | 封装 OWASP AntiSamy，加载策略文件，执行清洗并处理特殊字符乱码。 |

## 三、核心实现分析

### 1. XssHttpServletFilter：入口过滤器

过滤器非常简单，仅做一件事：将原始 `ServletRequest` 转型为 `HttpServletRequest`，然后创建 `XssHttpServletRequestWrapper` 实例，并将包装后的对象传递给过滤链。

```java
XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(request);
filterChain.doFilter(xssRequest, servletResponse);
```

> 注意：过滤器本身不执行任何清洗逻辑，所有清洗工作都委托给包装器。

### 2. XssHttpServletRequestWrapper：透明清洗

该包装器继承 `HttpServletRequestWrapper`，利用装饰器模式，对需要拦截的方法进行增强：

- **`getHeader(String name)`**：清洗单个请求头值。
- **`getParameter(String name)`**：清洗单个请求参数。
- **`getParameterValues(String name)`**：清洗同名参数的字符串数组。
- **`getParameterMap()`**：清洗整个参数映射表，每个值数组都会被递归清洗。

所有清洗最终调用 `XssUtils.cleaning()`。对于空值或空白字符串，直接返回原值，避免无谓的清洗开销。

### 3. XssUtils：AntiSamy 的核心封装

#### a) 单例与策略加载

`XssUtils` 采用双重检查锁实现线程安全的单例。构造时会从 classpath 加载策略文件 `antisamy/antisamy-anythinggoes.xml`：

```java
URL url = ResourceUtils.getURL("classpath:antisamy/antisamy-anythinggoes.xml");
return Policy.getInstance(url);
```

若加载失败（例如文件缺失），则回退到 AntiSamy 默认策略（较为严格）。

#### b) 清洗流程（`cleaning(String taintedHTML)`）

1. **反转义 HTML 实体**  
   调用 `StringEscapeUtils.unescapeHtml4()` 对输入进行反转义。这一步非常关键：AntiSamy 内部在扫描时会再次对特殊字符进行转义，如果不提前反转义，会导致重复转义，例如 `&lt;` 最终变成 `&amp;lt;`。

2. **调用 AntiSamy 扫描**  
   得到 `CleanResults` 并提取清洗后的 HTML。

3. **修复乱码**  
   AntiSamy 在某些环境下会把 `&nbsp;` 处理成不可读的乱码，也会把双引号转换成 `&quot;`。代码通过构造时预先清洗 `"&nbsp;"` 和 `"\""` 得到对应的乱码字符串（`nbsp` 和 `quot` 成员变量），然后用 `replaceAll` 将它们分别还原为空字符串和原始双引号。

4. **移除换行符**  
   最后删除所有换行符（`\n`），使输出更紧凑（这一行为是可选的，可根据需求调整）。

#### c) 异常处理

若 `scan()` 抛出 `ScanException` 或 `PolicyException`，则直接返回原始字符串，并记录调试日志，保证服务不中断。

## 四、工作流程

下图展示了从请求进入过滤器到返回清洗后数据的完整流程：

```text
[HTTP 请求] 
    → XssHttpServletFilter 
    → 包装为 XssHttpServletRequestWrapper 
    → 业务代码调用 request.getParameter("comment")
    → XssHttpServletRequestWrapper.getParameter() 
    → XssUtils.cleaning(原始输入)
         ├─ unescapeHtml4
         ├─ antiSamy.scan
         └─ 修复 &nbsp; / 双引号 / 换行符
    → 返回清洗后的字符串
    → 业务代码使用安全数据
```

## 五、配置与使用

### 1. 添加依赖

需要引入以下核心依赖（Maven 示例）：

```xml
<dependency>
    <groupId>org.owasp.antisamy</groupId>
    <artifactId>antisamy</artifactId>
    <version>1.7.4</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-text</artifactId>
    <version>1.11.0</version>
</dependency>
<!-- Spring 工具包（用于加载 classpath 资源） -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>6.0.0</version>
</dependency>
```

### 2. 策略文件放置

在 `src/main/resources/antisamy/` 目录下放置 `antisamy-anythinggoes.xml`（可从此处下载：[OWASP AntiSamy 策略文件](https://owasp.org/www-project-antisamy/)）。你也可以根据业务需求自定义策略，例如允许某些富文本标签（`<b>`、`<i>`、`<img>`）但禁止 `onload` 等事件属性。

### 3. 注册过滤器

#### 方式一：web.xml

```xml
<filter>
    <filter-name>xssFilter</filter-name>
    <filter-class>tutorials4j.framework.web.mvc.filter.XssHttpServletFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>xssFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

#### 方式二：Spring Boot 配置

```java
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<XssHttpServletFilter> xssFilterRegistration() {
        FilterRegistrationBean<XssHttpServletFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new XssHttpServletFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
```

## 六、高级处理细节

### 1. 数组参数的清洗

`getParameterValues` 返回 `String[]`，代码使用 `Arrays.stream().map(XssUtils::cleaning).collect(Collectors.toList())` 对每个元素单独清洗，保证了多选框、多值参数的全面防护。

### 2. getParameterMap 的完整清洗

重写 `getParameterMap()` 时，直接对 `Map<String, String[]>` 的每个 `value` 调用 `cleaning(String[])`，确保无论业务代码通过哪种方式获取参数，都能得到清洗后的数据。

### 3. 请求头清洗

常见的XSS攻击也可能出现在 `User-Agent`、`Referer` 等头字段，因此 `getHeader` 也进行了同样的清洗。

### 4. 乱码修复的奥秘

在 `XssUtils` 构造器中，分别用 `cleanHtml("&nbsp;")` 和 `cleanHtml("\"")` 获取 AntiSamy 处理后的结果。例如 `cleanHtml("\"")` 可能返回 `&quot;` 或者乱码 `ï¿½`，后续 `cleaning` 方法中会将这些特定乱码替换回预期字符。这个技巧有效解决了不同 AntiSamy 版本带来的编码不一致问题。

## 七、优缺点分析

### 优点

1. **安全性高**：基于白名单策略，只允许策略文件中定义的标签/属性，能防御绝大多数XSS攻击。
2. **对业务透明**：只需添加一个过滤器，无需修改任何业务 Controller 代码。
3. **灵活可配置**：通过替换策略文件，可以精细控制允许的 HTML 内容（如富文本编辑器输出）。
4. **覆盖面广**：同时清洗参数、参数数组、参数Map和请求头。

### 缺点

1. **性能开销**：每个请求参数都会经过 AntiSamy 扫描，在大流量场景下可能成为瓶颈。
2. **可能破坏合法输入**：对于需要提交 HTML 代码的场景（如博客内容、论坛帖子），策略过于严格会误删标签。解决方案是定制策略文件，或对特定接口跳过过滤器。
3. **仅处理 GET/POST 参数**：无法处理 `application/json` 格式的请求体（例如 RESTful API）。如需支持 JSON，需要额外扩展（例如在 `HttpServletRequest.getInputStream` 中进行处理）。
4. **策略文件依赖**：必须确保 `antisamy-anythinggoes.xml` 存在于 classpath 中，否则回退到默认策略可能导致业务异常。

## 八、优化建议

- **可配置的过滤开关**：允许通过注解或路径匹配跳过某些接口的XSS过滤。
- **缓存清洗结果**：对相同的原始字符串进行缓存，避免重复扫描（需注意内存占用）。
- **支持请求体 JSON**：重写 `getReader()` 和 `getInputStream()`，读取 Body 后使用 Jackson 解析并递归清洗字符串字段。
- **非侵入式记录攻击日志**：在 `XssUtils.cleaning()` 中当检测到输入被修改时，记录告警日志，便于安全审计。

## 九、总结

本文提供的 XSS 防护过滤器利用 OWASP AntiSamy 的强大策略引擎，以 Servlet 过滤器 + 请求包装器的典型模式，为 Java Web 应用加装了一层可靠的“免疫系统”。通过短短几个类，实现了对请求参数和头信息的全量清洗，且不影响原有业务逻辑。虽然存在一定的性能开销和对富文本场景的局限性，但对于大多数传统表单提交、评论、搜索等非富文本输入场景，是一个非常实用的安全加固方案。

如果你的项目正在寻找一种轻量、标准化且可细粒度控制的XSS防御手段，不妨尝试集成这套过滤器。

阅读最新文章，请关注我的微信公众号`杨运交` 