package tutorials4j.toolkit.core.function;

import java.util.Map;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface TaskRunner {
  void run(Map<String, String> params);
}
