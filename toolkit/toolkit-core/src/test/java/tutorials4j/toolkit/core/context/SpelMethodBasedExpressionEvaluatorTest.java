package tutorials4j.toolkit.core.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * {@link SpelMethodBasedExpressionEvaluator} 单元测试。
 *
 * @author YunJiao Yang
 */
class SpelMethodBasedExpressionEvaluatorTest {
  private static final String METHOD_NAME = "greet";

  private GenericApplicationContext applicationContext;
  private SpelMethodBasedExpressionEvaluator evaluator;

  @BeforeEach
  void setUp() {
    applicationContext = new GenericApplicationContext();
    applicationContext
        .getEnvironment()
        .getPropertySources()
        .addFirst(new MapPropertySource("test", Map.of("app.name", "tutorials4j")));
    applicationContext.registerBean("greeting", String.class, () -> "hello");
    applicationContext.refresh();

    evaluator = new SpelMethodBasedExpressionEvaluator();
    evaluator.setBeanFactory(applicationContext);
    evaluator.setEmbeddedValueResolver(applicationContext.getBeanFactory()::resolveEmbeddedValue);
  }

  @AfterEach
  void tearDown() {
    applicationContext.close();
  }

  private static Method greetMethod() throws NoSuchMethodException {
    return SampleService.class.getMethod(METHOD_NAME, String.class, int.class);
  }

  private static Object[] arguments() {
    return new Object[] {"ab", 3};
  }

  @Test
  @DisplayName("可按参数名引用方法参数")
  void shouldResolveParameterByName() throws NoSuchMethodException {
    Method method = greetMethod();

    assertThat(evaluator.getValue(method, arguments(), "#name", String.class)).isEqualTo("ab");
    assertThat(evaluator.getValue(method, arguments(), "#times", Integer.class)).isEqualTo(3);
  }

  @Test
  @DisplayName("可通过 #root.args 按下标引用方法参数")
  void shouldResolveParameterByIndex() throws NoSuchMethodException {
    Method method = greetMethod();

    assertThat(evaluator.getValue(method, arguments(), "#root.args[0]", String.class))
        .isEqualTo("ab");
    assertThat(evaluator.getValue(method, arguments(), "#root.args[1]", Integer.class))
        .isEqualTo(3);
  }

  @Test
  @DisplayName("#root.methodName 暴露被拦截的方法")
  void shouldExposeRootMethod() throws NoSuchMethodException {
    Method method = greetMethod();

    assertThat(evaluator.getValue(method, arguments(), "#root.methodName.name", String.class))
        .isEqualTo(METHOD_NAME);
  }

  @Test
  @DisplayName("可传入自定义变量参与表达式计算")
  void shouldResolveVariables() throws NoSuchMethodException {
    Method method = greetMethod();

    Integer result =
        evaluator.getValue(
            method, arguments(), "#times * #factor", Integer.class, Map.of("factor", 14));

    assertThat(result).isEqualTo(42);
  }

  @Test
  @DisplayName("未指定变量时使用空变量表")
  void shouldUseEmptyVariablesByDefault() throws NoSuchMethodException {
    Method method = greetMethod();

    assertThat(evaluator.getValue(method, arguments(), "#name.length()", Integer.class))
        .isEqualTo(2);
  }

  @Test
  @DisplayName("结果按目标类型转换")
  void shouldConvertResultType() throws NoSuchMethodException {
    Method method = greetMethod();

    assertThat(evaluator.getValue(method, arguments(), "#times", String.class)).isEqualTo("3");
  }

  @Test
  @DisplayName("可通过 @beanName 引用 Spring 容器中的 Bean")
  void shouldResolveBeanByName() throws NoSuchMethodException {
    Method method = greetMethod();

    assertThat(evaluator.getValue(method, arguments(), "@greeting", String.class))
        .isEqualTo("hello");
  }

  @Test
  @DisplayName("未注入 BeanFactory 时引用 Bean 抛出 SpelEvaluationException")
  void shouldFailBeanLookupWithoutBeanFactory() throws NoSuchMethodException {
    Method method = greetMethod();
    SpelMethodBasedExpressionEvaluator standalone = new SpelMethodBasedExpressionEvaluator();
    standalone.setEmbeddedValueResolver(value -> value);

    assertThatThrownBy(() -> standalone.getValue(method, arguments(), "@greeting", String.class))
        .isInstanceOf(SpelEvaluationException.class);
  }

  @Test
  @DisplayName("表达式中的占位符会先被解析")
  void shouldResolveEmbeddedPlaceholder() throws NoSuchMethodException {
    Method method = greetMethod();

    assertThat(evaluator.getValue(method, arguments(), "'${app.name}'", String.class))
        .isEqualTo("tutorials4j");
  }

  @Test
  @DisplayName("相同表达式只解析一次（表达式缓存）")
  void shouldCacheParsedExpression() {
    SpelExpressionParser parser = new SpelExpressionParser();

    Expression first = evaluator.parseExpression("1 + 1", parser);
    Expression second = evaluator.parseExpression("1 + 1", parser);

    assertThat(first).isNotNull();
    assertThat(second).isSameAs(first);
  }

  @Test
  @DisplayName("占位符解析结果为 null 时抛出 IllegalArgumentException")
  void shouldFailWhenResolverReturnsNull() {
    evaluator.setEmbeddedValueResolver(value -> null);
    SpelExpressionParser parser = new SpelExpressionParser();

    assertThatThrownBy(() -> evaluator.parseExpression("1 + 1", parser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Expression must not be null");
  }

  @Test
  @DisplayName("语法错误的表达式抛出 ParseException")
  void shouldFailOnInvalidExpression() throws NoSuchMethodException {
    Method method = greetMethod();

    assertThatThrownBy(() -> evaluator.getValue(method, arguments(), "1 +", String.class))
        .isInstanceOf(ParseException.class);
  }

  @Test
  @DisplayName("引用未定义的变量时结果为 null")
  void shouldReturnNullForUnknownVariable() throws NoSuchMethodException {
    Method method = greetMethod();

    assertThat(evaluator.getValue(method, arguments(), "#unknownProperty", String.class)).isNull();
  }

  @Test
  @DisplayName("调用不存在的方法抛出 SpelEvaluationException")
  void shouldFailOnUnknownMethod() throws NoSuchMethodException {
    Method method = greetMethod();

    assertThatThrownBy(
            () -> evaluator.getValue(method, arguments(), "nonExistentMethod()", String.class))
        .isInstanceOf(SpelEvaluationException.class);
  }

  @Test
  @DisplayName("方法参数持有者记录参数与方法")
  void shouldExposeMethodArgsHolder() throws NoSuchMethodException {
    Method method = greetMethod();

    SpelMethodBasedExpressionEvaluator.MethodArgsHolder holder =
        new SpelMethodBasedExpressionEvaluator.MethodArgsHolder(arguments(), method);

    assertThat(holder.args()).containsExactly("ab", 3);
    assertThat(holder.methodName()).isSameAs(method);
  }

  static class SampleService {
    public String greet(String name, int times) {
      return name.repeat(times);
    }
  }
}
