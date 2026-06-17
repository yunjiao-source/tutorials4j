package tutorials4j.framework.schedule.core.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.util.Assert;
import tutorials4j.framework.schedule.core.bean.YamlTask;
import tutorials4j.framework.schedule.core.properties.ScheduleProperties;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class YamlTaskRepository implements TaskRepository<YamlTask> {
  private final ConcurrentMap<String, YamlTask> taskMap = new ConcurrentHashMap<>();

  public YamlTaskRepository(ScheduleProperties properties) {
    properties
        .getTasks()
        .forEach(
            (k, v) -> {
              if (!v.validate()) {
                throw new IllegalArgumentException(
                    "The task data validation has failed. Please review the configuration file.");
              }
              YamlTask yamlTask = YamlTask.of(v);
              yamlTask.setTaskCode(k);
              taskMap.put(k, yamlTask);
            });
  }

  @Override
  public Optional<YamlTask> findByTaskCode(String taskCode) {
    Assert.hasText(taskCode, "taskCode must not be null or empty");
    return Optional.ofNullable(taskMap.get(taskCode));
  }

  @Override
  public List<YamlTask> findAll() {
    return new ArrayList<>(taskMap.values());
  }
}
