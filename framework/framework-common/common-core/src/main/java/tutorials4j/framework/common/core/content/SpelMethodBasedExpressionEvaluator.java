package tutorials4j.framework.common.core.content;

import java.lang.reflect.Method;
import java.util.Map;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MapAccessor;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringValueResolver;

/**
 * 基于 Spring SpEL 的方法参数表达式求值器实现。
 *
 * <p>该实现利用 Spring 的 {@link MethodBasedEvaluationContext} 将方法参数暴露为 SpEL 变量， 支持从 Spring BeanFactory
 * 解析 Bean 引用、从 {@link StringValueResolver} 解析嵌入的值（如属性占位符）。 内部维护表达式缓存以提高重复求值性能。
 *
 * <p><b>特性：</b>
 *
 * <ul>
 *   <li>自动将方法参数通过参数名暴露（借助 {@link DefaultParameterNameDiscoverer}）
 *   <li>支持通过 {@code #root} 对象访问方法调用相关信息
 *   <li>支持 {@link Map} 类型的属性访问（通过 {@link MapAccessor}）
 *   <li>支持 Bean 引用（例如 {@code @myBean.method()}）
 *   <li>支持嵌入式值解析（例如 {@code ${some.property}}）
 * </ul>
 *
 * @author Yun Jiao
 * @see MethodBasedExpressionEvaluator
 * @see MethodBasedEvaluationContext
 */
public class SpelMethodBasedExpressionEvaluator
    implements MethodBasedExpressionEvaluator, EmbeddedValueResolverAware, BeanFactoryAware {

  /** 用于 {@link Map} 类型的属性访问器（线程安全，可复用） */
  private static final MapAccessor MAP_ACCESSOR = new MapAccessor();

  /** 表达式解析结果缓存，使用弱引用键避免内存泄漏 */
  private final Map<String, Expression> expressionCache = new ConcurrentReferenceHashMap<>(16);

  /** SpEL 表达式解析器，线程安全 */
  private final ExpressionParser expressionParser = new SpelExpressionParser();

  /** 参数名发现器，用于从方法字节码或调试信息中获取参数名称 */
  private final ParameterNameDiscoverer parameterNameDiscoverer =
      new DefaultParameterNameDiscoverer();

  /** Bean 解析器，用于在表达式中引用 Spring 容器中的 Bean */
  private BeanResolver beanResolver;

  /** 嵌入式值解析器（例如解析 ${...} 占位符），通过 {@link EmbeddedValueResolverAware} 注入 */
  @Setter private StringValueResolver embeddedValueResolver;

  @Override
  public <T> T getValue(
      Method method,
      Object[] arguments,
      String expression,
      Class<T> resultType,
      @NonNull Map<String, Object> variables) {
    EvaluationContext context = createEvaluationContext(method, arguments);
    if (!variables.isEmpty()) {
      variables.forEach(context::setVariable);
    }
    Expression exp = parseExpression(expression, expressionParser);
    return exp.getValue(context, resultType);
  }

  /**
   * 创建 SpEL 求值上下文，该方法将目标方法和参数包装为表达式可访问的根对象。
   *
   * <p>上下文中会注册：Bean 解析器（如果已设置）、{@link MapAccessor} 以及方法参数变量。
   *
   * @param method 目标方法
   * @param args 实际参数数组
   * @return 配置好的 {@link MethodBasedEvaluationContext} 实例
   */
  protected EvaluationContext createEvaluationContext(Method method, Object[] args) {
    MethodBasedEvaluationContext context =
        new MethodBasedEvaluationContext(
            new MethodArgsHolder(args, method), method, args, parameterNameDiscoverer);
    context.setBeanResolver(beanResolver);
    context.addPropertyAccessor(MAP_ACCESSOR);
    return context;
  }

  /**
   * 解析并缓存表达式。
   *
   * <p>首先使用 {@code embeddedValueResolver} 解析表达式中的占位符（如果存在）， 然后使用 {@link ExpressionParser} 将字符串解析为
   * {@link Expression} 对象，并缓存结果。
   *
   * @param expression 原始表达式字符串（可能包含占位符）
   * @param parser SpEL 解析器
   * @return 解析后的 {@link Expression} 对象
   * @throws IllegalArgumentException 如果解析后的表达式为 {@code null}
   */
  protected Expression parseExpression(String expression, ExpressionParser parser) {
    return expressionCache.computeIfAbsent(
        expression,
        exp -> {
          exp = embeddedValueResolver.resolveStringValue(exp);
          Assert.notNull(exp, "Expression must not be null");
          return parser.parseExpression(exp);
        });
  }

  @Override
  public void setBeanFactory(@NonNull BeanFactory beanFactory) {
    beanResolver = new BeanFactoryResolver(beanFactory);
  }

  // 辅助类，用于支持 #root.args[0]
  public record MethodArgsHolder(Object[] args, Object methodName) {}
}
