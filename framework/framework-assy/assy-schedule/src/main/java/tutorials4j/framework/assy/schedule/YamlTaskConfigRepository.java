package tutorials4j.framework.assy.schedule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import tutorials4j.framework.assy.core.properties.TaskOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class YamlTaskConfigRepository implements TaskConfigRepository {
  private final Map<String, TaskOptions> tasks = new HashMap<>();

  public YamlTaskConfigRepository(Collection<TaskOptions> tasks) {
    this.tasks.putAll(tasks.stream().collect(Collectors.toMap(TaskOptions::getName, m -> m)));
  }

  @Override
  public Optional<TaskOptions> getTaskConfig(String name) {
    return Optional.ofNullable(tasks.get(name));
  }

  @Override
  public Collection<TaskOptions> getAllCronExpressions() {
    return tasks.values();
  }
}
