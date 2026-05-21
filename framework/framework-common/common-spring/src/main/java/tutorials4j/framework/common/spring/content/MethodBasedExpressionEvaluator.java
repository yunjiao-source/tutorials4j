package tutorials4j.framework.common.spring.content;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import org.springframework.lang.NonNull;

/**
 * 基于方法参数和表达式的值求值器。
 *
 * <p>根据给定的目标方法、实际参数、SpEL 表达式以及可选的变量上下文，计算表达式并返回指定类型的值。 适用于在方法调用上下文中动态解析表达式（例如权限注解、日志模板等场景）。
 *
 * @author Yun Jiao
 */
public interface MethodBasedExpressionEvaluator {

  /**
   * 在指定方法调用上下文中对表达式求值，并返回指定类型的值。
   *
   * @param method 被执行的目标方法，用于获取参数名等元信息
   * @param arguments 方法调用时传入的实际参数数组
   * @param expression SpEL 表达式字符串
   * @param resultType 期望的返回值类型
   * @param variables 额外的变量映射，这些变量可在表达式中通过 #{variableName} 形式访问
   * @param <T> 返回值类型
   * @return 表达式求值结果，类型与 {@code resultType} 一致
   * @throws org.springframework.expression.EvaluationException 表达式解析或求值失败时抛出
   */
  <T> T getValue(
      Method method,
      Object[] arguments,
      String expression,
      Class<T> resultType,
      @NonNull Map<String, Object> variables);

  /**
   * 在指定方法调用上下文中对表达式求值，不使用额外变量。
   *
   * @param method 被执行的目标方法
   * @param arguments 方法调用时传入的实际参数数组
   * @param expression SpEL 表达式字符串
   * @param resultType 期望的返回值类型
   * @param <T> 返回值类型
   * @return 表达式求值结果
   * @see #getValue(Method, Object[], String, Class, Map)
   */
  default <T> T getValue(
      Method method, Object[] arguments, String expression, Class<T> resultType) {
    return getValue(method, arguments, expression, resultType, Collections.emptyMap());
  }
}
