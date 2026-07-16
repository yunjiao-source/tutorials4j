# [034][公共模块]基于SpEL的方法参数表达式求值器设计与实现

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

## 一、概述

在Spring框架驱动的应用开发中，动态表达式求值是一个常见需求，例如权限注解（`@PreAuthorize`）、缓存注解（`@Cacheable`）、日志模板等场景，都需要根据当前方法的调用上下文（方法名、参数值、返回值等）计算表达式结果。

本文介绍的 `MethodBasedExpressionEvaluator` 是一套优雅的抽象设计，其实现 `SpelMethodBasedExpressionEvaluator` 基于Spring的SpEL（Spring Expression Language）引擎，将目标方法的参数、Bean容器、自定义变量等无缝暴露给表达式，实现高内聚、低耦合的动态求值能力。

## 二、核心功能

该组件的核心功能可以概括为：

1. **方法上下文感知**：自动将目标方法的参数名（通过参数名发现器获取）映射为表达式中的变量，例如表达式 `#userId` 可直接访问名为 `userId` 的方法参数。
2. **占位符解析**：支持表达式中的占位符（如 `${app.timeout}`），通过 `EmbeddedValueResolverAware` 机制解析外部配置。
3. **Bean引用**：表达式可引用Spring容器中的Bean，例如 `@userService.findName(#id)`。
4. **额外变量注入**：允许调用方传入自定义的变量映射，扩展表达式可访问的上下文。
5. **表达式缓存**：对解析后的SpEL表达式对象进行缓存，避免重复解析开销。
6. **类型安全求值**：直接返回指定类型的求值结果，减少强制类型转换。

## 三、接口设计分析

```java
public interface MethodBasedExpressionEvaluator {
    <T> T getValue(Method method, Object[] arguments, 
                   String expression, Class<T> resultType,
                   @NonNull Map<String, Object> variables);
    
    default <T> T getValue(Method method, Object[] arguments,
                           String expression, Class<T> resultType) {
        return getValue(method, arguments, expression, resultType, Collections.emptyMap());
    }
}
```

接口设计体现了**单一职责**和**开闭原则**：
- 核心方法接收完整的求值上下文（`Method` + `arguments` + `variables`），保证灵活性。
- 默认方法提供简化版本，方便无需额外变量的场景。
- 泛型方法 `T getValue(...)` 确保调用方可直接获得目标类型结果，无需手动转换。

## 四、实现类深度解析

### 4.1 类结构与依赖

```java
public class SpelMethodBasedExpressionEvaluator
    implements MethodBasedExpressionEvaluator, 
               EmbeddedValueResolverAware, 
               BeanFactoryAware
```

实现类同时实现了两个Spring感知接口：
- `EmbeddedValueResolverAware`：注入 `StringValueResolver`，用于解析 `${...}` 占位符。
- `BeanFactoryAware`：注入 `BeanFactory`，创建 `BeanFactoryResolver` 使表达式支持 `@bean` 引用。

核心组件：

| 组件 | 类型 | 作用 |
|------|------|------|
| `expressionCache` | `Map<String, Expression>` | 线程安全的表达式缓存（基于 `ConcurrentReferenceHashMap`，弱引用防止内存泄漏） |
| `expressionParser` | `SpelExpressionParser` | SpEL解析器，线程安全且可复用 |
| `parameterNameDiscoverer` | `DefaultParameterNameDiscoverer` | 从方法字节码或本地变量表获取参数名 |
| `beanResolver` | `BeanResolver` | 用于表达式中解析Bean引用 |
| `embeddedValueResolver` | `StringValueResolver` | 解析占位符 |

### 4.2 求值主流程

`getValue` 方法执行步骤：

1. **创建求值上下文**：调用 `createEvaluationContext(method, arguments)` 构建 `MethodBasedEvaluationContext`。
2. **注入额外变量**：将 `variables` 映射中的键值对设置到上下文中（`context.setVariable(key, value)`）。
3. **解析表达式**：调用 `parseExpression` 完成占位符解析 + SpEL解析 + 缓存。
4. **求值并返回**：`exp.getValue(context, resultType)` 返回指定类型结果。

### 4.3 关键实现细节

#### 4.3.1 方法参数的暴露

`MethodBasedEvaluationContext` 是Spring提供的专用上下文实现，它根据 `Method` 对象和实际参数数组，自动将每个参数注册为变量，变量名即参数名。参数名的获取依赖于 `ParameterNameDiscoverer`，默认实现 `DefaultParameterNameDiscoverer` 会尝试从以下几种途径获取：
- 调试信息中的局部变量表（需编译时保留 `-parameters` 或 `-g`）
- 可选的 `@ParameterName` 注解（Spring 4+）
- 回退到 `arg0`, `arg1` 等占位符

#### 4.3.2 表达式缓存机制

```java
protected Expression parseExpression(String expression, ExpressionParser parser) {
    return expressionCache.computeIfAbsent(expression, exp -> {
        exp = embeddedValueResolver.resolveStringValue(exp);
        Assert.notNull(exp, "Expression must not be null");
        return parser.parseExpression(exp);
    });
}
```

- 缓存键为原始表达式字符串（未解析占位符之前）。不同表达式可共享解析结果；但若占位符解析结果因外部配置变化而变化，缓存可能导致不一致——通常占位符代表的应用配置是静态的，因此可以接受。
- `ConcurrentReferenceHashMap` 使用弱引用键，避免缓存长期持有表达式字符串导致内存无法回收。

#### 4.3.3 属性访问器扩展

```java
context.addPropertyAccessor(MAP_ACCESSOR);
```

`MapAccessor` 使得表达式可以像访问对象属性一样访问 `Map` 的键值，例如 `#myMap.keyName`。这增强了表达式对非Bean对象的友好性。

#### 4.3.4 BeanFactoryAware 与 BeanResolver

```java
@Override
public void setBeanFactory(@NonNull BeanFactory beanFactory) {
    beanResolver = new BeanFactoryResolver(beanFactory);
}
```

`BeanFactoryResolver` 实现了 `BeanResolver` 接口，支持表达式中使用 `@` 前缀引用Bean，例如 `@userService.getCurrentUser()`。这为表达式提供了调用任何Spring Bean方法的能力。

## 五、使用示例

### 5.1 基础用法：访问方法参数

假设有一个服务方法：
```java
public User findUser(Long userId, String name) { ... }
```

使用求值器：
```java
SpelMethodBasedExpressionEvaluator evaluator = new SpelMethodBasedExpressionEvaluator();
Method method = UserService.class.getMethod("findUser", Long.class, String.class);
Object[] args = {1001L, "张三"};
Long result = evaluator.getValue(method, args, "#userId + 1", Long.class);
// result = 1002
```

### 5.2 带占位符的表达式

配置文件中定义 `query.limit=100`，表达式为 `"${query.limit} > #limit ? #limit : ${query.limit}"`：
```java
evaluator.setEmbeddedValueResolver(new StandardEnvironment().getPlaceholderResolver(...));
Integer limit = evaluator.getValue(method, new Object[]{50}, "${query.limit} > #limit ? #limit : ${query.limit}", Integer.class);
// 若 limit 参数为 50，结果为 50（取较小值）
```

### 5.3 引用Spring Bean

```java
evaluator.setBeanFactory(applicationContext);
// 表达式调用 Bean 方法
Boolean isValid = evaluator.getValue(method, args, "@validationService.check(#userId)", Boolean.class);
```

### 5.4 注入额外变量

```java
Map<String, Object> vars = new HashMap<>();
vars.put("tenantId", "tenant_001");
String tenant = evaluator.getValue(method, args, "#tenantId", String.class, vars);
```

## 六、适用场景

| 场景 | 说明 |
|------|------|
| 权限控制 | 在方法拦截器中，根据方法参数和当前用户信息计算是否有权执行 |
| 审计日志 | 动态生成日志内容，例如 `"用户 #userId 执行了删除操作"` |
| 数据权限 | 在查询方法前动态追加过滤条件，根据参数值决定可见数据范围 |
| 缓存Key生成 | SpEL表达式作为 `@Cacheable` 的 `key` 属性，灵活组合参数值 |
| 消息模板 | 根据方法参数填充通知内容 |

## 七、注意事项与最佳实践

1. **参数名可用性**：确保编译时保留参数名（`-parameters` 选项）或使用 `@ParameterName` 注解，否则会退化为 `arg0`, `arg1` 等不友好名称。
2. **表达式安全**：当表达式来自不可信源（如用户输入）时，可能引发恶意表达式注入风险。应使用 `SimpleEvaluationContext` 限制功能，但当前实现使用标准 `MethodBasedEvaluationContext`，需注意控制表达式来源。
3. **性能考量**：表达式解析结果已缓存，但占位符解析未缓存。如果占位符值频繁变化，应考虑更细粒度的缓存策略。
4. **Null安全**：表达式求值结果可能为 `null`，调用方需做判空处理。
5. **线程安全性**：`SpelMethodBasedExpressionEvaluator` 是无状态的（缓存为 `ConcurrentHashMap`，解析器线程安全），可以被多个线程共享使用。

## 八、总结

`SpelMethodBasedExpressionEvaluator` 是一个设计精良的表达式求值组件，它充分利用了Spring SpEL的强大能力，并针对方法调用场景做了深度适配。通过内置的参数暴露、占位符解析、Bean引用、表达式缓存等特性，它为上层框架（如权限控制、缓存、日志等）提供了一个通用、高效、可扩展的求值基础设施。

在实际项目中，可以将其作为Bean配置在Spring容器中，并在需要动态表达式解析的地方（如自定义注解处理器、拦截器）注入使用。该组件也体现了Spring设计哲学：约定优于配置，扩展点开放，内部实现可靠。
