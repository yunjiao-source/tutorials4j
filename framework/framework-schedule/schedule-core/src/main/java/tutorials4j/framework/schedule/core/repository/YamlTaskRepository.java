package tutorials4j.framework.schedule.core.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import tutorials4j.framework.schedule.core.bean.YamlTask;
import tutorials4j.framework.schedule.core.exception.ScheduleException;
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
                throw new IllegalArgumentException("taskOptions is not valid");
              }
              YamlTask yamlTask = YamlTask.of(v);
              yamlTask.setName(k);
              taskMap.put(k, yamlTask);
            });
  }

  @Override
  public void create(YamlTask createTask) {
    assertTask(createTask);

    String name = createTask.getName();
    YamlTask task = taskMap.putIfAbsent(name, createTask);
    if (task != null) {
      // 键已存在且对应的值不为null
      throw new ScheduleException("重复创建任务:" + name);
    }
  }

  @Override
  public void update(YamlTask updateTask) {
    assertTask(updateTask);

    String name = updateTask.getName();
    findByName(name)
        .map(
            task -> {
              BeanUtils.copyProperties(updateTask, task, "name");
              taskMap.computeIfPresent(name, (k, v) -> task);
              return task;
            })
        .orElseThrow(() -> new ScheduleException("任务不存在:" + name));
  }

  @Override
  public boolean delete(String name) {
    Assert.hasText(name, "name must not be null or empty");
    return findByName(name).map(task -> taskMap.remove(task.getName(), task)).orElse(false);
  }

  @Override
  public Optional<YamlTask> findByName(String name) {
    Assert.hasText(name, "name must not be null or empty");
    return Optional.ofNullable(taskMap.get(name));
  }

  @Override
  public List<YamlTask> findAll() {
    return new ArrayList<>(taskMap.values());
  }

  private static void assertTask(YamlTask task) {
    Assert.notNull(task, "task must not be null");
    task.isInvalid();
  }
}
