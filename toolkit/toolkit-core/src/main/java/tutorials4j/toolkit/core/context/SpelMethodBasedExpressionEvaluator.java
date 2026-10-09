package tutorials4j.toolkit.core.context;

import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.MapAccessor;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringValueResolver;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class SpelMethodBasedExpressionEvaluator
    implements MethodBasedExpressionEvaluator, EmbeddedValueResolverAware, BeanFactoryAware {
  private static final MapAccessor MAP_ACCESSOR = new MapAccessor();
  private final Map<String, Expression> expressionCache = new ConcurrentReferenceHashMap<>(16);
  private final ExpressionParser expressionParser = new SpelExpressionParser();
  private final ParameterNameDiscoverer parameterNameDiscoverer =
      new DefaultParameterNameDiscoverer();
  private BeanResolver beanResolver;

  private StringValueResolver embeddedValueResolver;

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    beanResolver = new BeanFactoryResolver(beanFactory);
  }

  @Override
  public void setEmbeddedValueResolver(StringValueResolver resolver) {
    this.embeddedValueResolver = resolver;
  }

  @Override
  public <T> T getValue(
      Method method,
      Object[] arguments,
      String expression,
      Class<T> resultType,
      Map<String, Object> variables) {
    EvaluationContext context = createEvaluationContext(method, arguments);
    if (!variables.isEmpty()) {
      variables.forEach(context::setVariable);
    }
    Expression exp = parseExpression(expression, expressionParser);
    return exp.getValue(context, resultType);
  }

  protected EvaluationContext createEvaluationContext(Method method, Object[] args) {
    MethodBasedEvaluationContext context =
        new MethodBasedEvaluationContext(
            new MethodArgsHolder(args, method), method, args, parameterNameDiscoverer);
    context.setBeanResolver(beanResolver);
    context.addPropertyAccessor(MAP_ACCESSOR);
    return context;
  }

  protected Expression parseExpression(String expression, ExpressionParser parser) {
    return expressionCache.computeIfAbsent(
        expression,
        exp -> {
          exp = embeddedValueResolver.resolveStringValue(exp);
          Assert.notNull(exp, "Expression must not be null");
          return parser.parseExpression(exp);
        });
  }

  public record MethodArgsHolder(Object[] args, Object methodName) {}
}
