package tutorials4j.framework.schedule.core.bean;

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
