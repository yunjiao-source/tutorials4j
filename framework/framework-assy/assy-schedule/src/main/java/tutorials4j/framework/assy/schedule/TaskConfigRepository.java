package tutorials4j.framework.assy.schedule;

import java.util.Collection;
import java.util.Optional;
import tutorials4j.framework.assy.core.properties.TaskOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface TaskConfigRepository {
  Optional<TaskOptions> getTaskConfig(String name);

  Collection<TaskOptions> getAllCronExpressions();
}
