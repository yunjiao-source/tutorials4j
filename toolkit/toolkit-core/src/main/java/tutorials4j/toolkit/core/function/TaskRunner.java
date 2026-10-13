package tutorials4j.toolkit.core.function;

import java.util.Map;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@FunctionalInterface
public interface TaskRunner {
  void run(Map<String, String> params);
}
