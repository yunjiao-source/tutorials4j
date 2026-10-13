package tutorials4j.toolkit.core.context;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public interface MethodBasedExpressionEvaluator {
  <T> T getValue(
      Method method,
      Object[] arguments,
      String expression,
      Class<T> resultType,
      Map<String, Object> variables);

  default <T> T getValue(
      Method method, Object[] arguments, String expression, Class<T> resultType) {
    return getValue(method, arguments, expression, resultType, Collections.emptyMap());
  }
}
