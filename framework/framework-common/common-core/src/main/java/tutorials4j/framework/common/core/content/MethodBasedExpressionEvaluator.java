package tutorials4j.framework.common.core.content;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import org.springframework.lang.NonNull;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface MethodBasedExpressionEvaluator {

  <T> T getValue(
      Method method,
      Object[] arguments,
      String expression,
      Class<T> resultType,
      @NonNull Map<String, Object> variables);

  default <T> T getValue(
      Method method, Object[] arguments, String expression, Class<T> resultType) {
    return getValue(method, arguments, expression, resultType, Collections.emptyMap());
  }
}
