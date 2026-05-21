package tutorials4j.framework.common.spring.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.util.StringValueResolver;

/**
 * 单元测试 {@link SpelMethodBasedExpressionEvaluator}。
 *
 * <p>测试覆盖：
 *
 * <ul>
 *   <li>基于方法参数（参数名及 #args 数组）的表达式求值
 *   <li>额外变量的传递与访问
 *   <li>Spring Bean 引用的解析（如 {@code @myBean.method()}）
 *   <li>嵌入式值解析器处理占位符（如 {@code ${...}}）
 *   <li>表达式缓存机制
 *   <li>{@link Map} 类型属性的访问
 *   <li>异常处理（无效表达式、类型转换错误等）
 *   <li>接口默认方法（无变量版本）
 * </ul>
 *
 * @author Yun Jiao
 */
class SpelMethodBasedExpressionEvaluatorTest {

  private SpelMethodBasedExpressionEvaluator evaluator;
  private BeanFactory mockBeanFactory;
  private StringValueResolver mockValueResolver;

  /** 测试用的服务类，用于验证 Bean 引用及普通方法参数 */
  @SuppressWarnings("unused")
  static class TestService {
    public String greet(String name) {
      return "Hello, " + name;
    }

    public int add(int a, int b) {
      return a + b;
    }

    public String getMessage() {
      return "message from bean";
    }

    public void voidMethod() {
      // no-op
    }
  }

  @BeforeEach
  void setUp() {
    evaluator = new SpelMethodBasedExpressionEvaluator();

    // 模拟 BeanFactory，用于 @beanName 引用
    mockBeanFactory = mock(BeanFactory.class);
    evaluator.setBeanFactory(mockBeanFactory);

    // 模拟嵌入式值解析器，默认行为：原样返回（不替换）
    mockValueResolver = mock(StringValueResolver.class);
    when(mockValueResolver.resolveStringValue(anyString())).thenAnswer(inv -> inv.getArgument(0));
    evaluator.setEmbeddedValueResolver(mockValueResolver);
  }

  // ==================== 方法参数暴露测试 ====================

  @Test
  void shouldAccessMethodParametersByArgumentName() throws Exception {
    // 准备测试方法及参数
    Method method = TestService.class.getMethod("greet", String.class);
    Object[] args = {"Alice"};

    // 通过参数名访问（依赖于 ParameterNameDiscoverer 成功解析参数名为 "name"）
    String expression = "#name";
    String result = evaluator.getValue(method, args, expression, String.class);

    assertThat(result).isEqualTo("Alice");
  }

  @Test
  void shouldAccessMethodParametersViaArgsArray() throws Exception {
    Method method = TestService.class.getMethod("add", int.class, int.class);
    Object[] args = {10, 20};

    // 使用 #args 数组访问（保证通用性）
    String expression = "#root.args[0] + #root.args[1]";
    Integer result = evaluator.getValue(method, args, expression, Integer.class);

    assertThat(result).isEqualTo(30);
  }

  // ==================== 额外变量测试 ====================

  @Test
  void shouldAccessAdditionalVariables() throws Exception {
    Method method = TestService.class.getMethod("greet", String.class);
    Object[] args = {"Alice"};

    Map<String, Object> variables = new HashMap<>();
    variables.put("suffix", "!!!");
    String expression = "#name + #suffix";

    String result = evaluator.getValue(method, args, expression, String.class, variables);
    assertThat(result).isEqualTo("Alice!!!");
  }

  @Test
  void shouldUseEmptyVariablesWhenUsingDefaultMethod() throws Exception {
    Method method = TestService.class.getMethod("add", int.class, int.class);
    Object[] args = {5, 7};

    // 调用接口的默认方法（无 variables 参数）
    String expression = "#root.args[0] * #root.args[1]";
    Integer result = evaluator.getValue(method, args, expression, Integer.class);

    assertThat(result).isEqualTo(35);
  }

  // ==================== Bean 引用测试 ====================

  @Test
  void shouldResolveBeanReferenceInExpression() throws Exception {
    // 准备一个真实的 Bean 实例
    TestService testBean = new TestService();
    when(mockBeanFactory.getBean("testBean")).thenReturn(testBean);

    Method method = TestService.class.getMethod("voidMethod");
    Object[] args = new Object[0];

    // 表达式引用 Bean 并调用其方法
    String expression = "@testBean.getMessage()";
    String result = evaluator.getValue(method, args, expression, String.class);

    assertThat(result).isEqualTo("message from bean");

    // 验证 BeanFactory 被正确调用
    verify(mockBeanFactory, only()).getBean("testBean");
  }

  @Test
  void shouldResolveBeanReferenceWithoutMethodCall() throws Exception {
    TestService testBean = new TestService();
    when(mockBeanFactory.getBean("testBean")).thenReturn(testBean);

    Method method = TestService.class.getMethod("voidMethod");
    Object[] args = new Object[0];

    // 直接获取 Bean 实例
    String expression = "@testBean";
    TestService result = evaluator.getValue(method, args, expression, TestService.class);

    assertThat(result).isSameAs(testBean);
  }

  // ==================== 嵌入式值解析器测试 ====================

  @Test
  void shouldHandleMultiplePlaceholders() throws Exception {
    when(mockValueResolver.resolveStringValue("${a} + ${b}")).thenReturn("10 + 20");

    Method method = TestService.class.getMethod("add", int.class, int.class);
    Object[] args = {100, 200}; // 表达式实际已变为常量，不再依赖 #args
    String expression = "${a} + ${b}";
    Integer result = evaluator.getValue(method, args, expression, Integer.class);

    assertThat(result).isEqualTo(30);
  }

  // ==================== 表达式缓存测试 ====================

  @Test
  void shouldCacheParsedExpressions() throws Exception {
    Method method = TestService.class.getMethod("add", int.class, int.class);
    Object[] args = {1, 2};

    String expression = "#root.args[0] + #root.args[1]";

    // 第一次求值
    evaluator.getValue(method, args, expression, Integer.class);
    // 第二次求值（相同表达式字符串）
    evaluator.getValue(method, args, expression, Integer.class);

    // 反射获取缓存 Map
    Field cacheField = SpelMethodBasedExpressionEvaluator.class.getDeclaredField("expressionCache");
    cacheField.setAccessible(true);
    @SuppressWarnings("unchecked")
    ConcurrentMap<String, Object> cache = (ConcurrentMap<String, Object>) cacheField.get(evaluator);

    // 缓存中应只有一个条目，且键为原始表达式（未解析占位符前的字符串）
    assertThat(cache).hasSize(1);
    assertThat(cache).containsKey(expression);

    // 验证解析器仅被调用一次（parseExpression 内部会调用 parser.parseExpression，但无法直接 mock parser，
    // 通过缓存大小和后续验证也可以确认）此处再验证第二次求值后缓存大小不变
    evaluator.getValue(method, args, expression, Integer.class);
    assertThat(cache).hasSize(1);
  }

  @Test
  void differentExpressionsAreCachedSeparately() throws Exception {
    Method method = TestService.class.getMethod("add", int.class, int.class);
    Object[] args = {1, 2};

    evaluator.getValue(method, args, "#root.args[0]", Integer.class);
    evaluator.getValue(method, args, "#root.args[1]", Integer.class);

    Field cacheField = SpelMethodBasedExpressionEvaluator.class.getDeclaredField("expressionCache");
    cacheField.setAccessible(true);
    @SuppressWarnings("unchecked")
    ConcurrentMap<String, Object> cache = (ConcurrentMap<String, Object>) cacheField.get(evaluator);

    assertThat(cache).hasSize(2);
  }

  // ==================== Map 属性访问器测试 ====================

  @Test
  void shouldAccessMapEntriesUsingPropertyAccessor() throws Exception {
    Method method = TestService.class.getMethod("voidMethod");
    Object[] args = new Object[0];

    Map<String, String> map = new HashMap<>();
    map.put("city", "Beijing");
    Map<String, Object> variables = new HashMap<>();
    variables.put("info", map);

    // 使用 map 访问器直接访问 key
    String expression = "#info.city";
    String result = evaluator.getValue(method, args, expression, String.class, variables);

    assertThat(result).isEqualTo("Beijing");
  }

  // ==================== 类型转换测试 ====================

  @Test
  void shouldConvertResultToExpectedType() throws Exception {
    Method method = TestService.class.getMethod("add", int.class, int.class);
    Object[] args = {5, 3};

    // 期望返回 String 类型，自动转换
    String expression = "#root.args[0] + #root.args[1]";
    String result = evaluator.getValue(method, args, expression, String.class);

    assertThat(result).isEqualTo("8"); // 字符串拼接，不是数字相加
  }

  // ==================== 异常情况测试 ====================

  @Test
  void shouldThrowExceptionForInvalidExpression() throws Exception {
    Method method = TestService.class.getMethod("add", int.class, int.class);
    Object[] args = {1, 2};
    String invalidExpression = "invalid syntax !@#";

    assertThatThrownBy(() -> evaluator.getValue(method, args, invalidExpression, Object.class))
        .isInstanceOf(SpelParseException.class)
        .hasMessageContaining("EL1041E");
  }

  @Test
  void shouldThrowExceptionWhenPlaceholderResolvesToNull() throws Exception {
    // 模拟解析器返回 null
    when(mockValueResolver.resolveStringValue("${missing}")).thenReturn(null);

    Method method = TestService.class.getMethod("voidMethod");
    Object[] args = new Object[0];
    String expression = "${missing}";

    assertThatThrownBy(() -> evaluator.getValue(method, args, expression, Object.class))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Expression must not be null");
  }

  @Test
  void shouldThrowExceptionWhenBeanNotFound() throws Exception {
    // 模拟 BeanFactory 找不到 Bean
    when(mockBeanFactory.getBean("nonExistent")).thenThrow(new RuntimeException("Bean not found"));

    Method method = TestService.class.getMethod("voidMethod");
    Object[] args = new Object[0];
    String expression = "@nonExistent";

    assertThatThrownBy(() -> evaluator.getValue(method, args, expression, Object.class))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Bean not found");
  }

  // ==================== 根对象访问测试（可选） ====================
  // 说明：MethodBasedEvaluationContext 将目标方法（Method 实例）作为根对象，
  // 表达式中可通过 #root 访问方法名等。此处简单验证该特性可用。

}
