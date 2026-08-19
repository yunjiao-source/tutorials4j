package tutorials4j.framework.schedule.spring.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.util.Assert;
import tutorials4j.framework.schedule.spring.bean.YamlTask;
import tutorials4j.framework.schedule.spring.properties.SpringScheduleProperties;

/**
 * 基于 YAML 配置的定时任务仓库实现。
 *
 * <p>构造时读取 {@link SpringScheduleProperties} 中的任务配置，校验后转换为 {@link YamlTask} 并缓存在内存 Map
 * 中，提供按任务编码查询与全量查询能力。
 *
 * @author Yun Jiao
 */
public class YamlTaskRepository implements TaskRepository<YamlTask> {
  /** 任务编码到任务的映射缓存。 */
  private final ConcurrentMap<String, YamlTask> taskMap = new ConcurrentHashMap<>();

  /**
   * 从配置属性中加载并校验所有任务。
   *
   * @param properties Spring 定时任务配置属性
   * @throws IllegalArgumentException 存在校验失败的任务时抛出
   */
  public YamlTaskRepository(SpringScheduleProperties properties) {
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

  /**
   * 根据任务编码查询任务。
   *
   * @param taskCode 任务编码，不能为空
   * @return 任务的可选结果；不存在时返回 {@link Optional#empty()}
   */
  @Override
  public Optional<YamlTask> findByTaskCode(String taskCode) {
    Assert.hasText(taskCode, "taskCode must not be null or empty");
    return Optional.ofNullable(taskMap.get(taskCode));
  }

  /**
   * 获取全部任务。
   *
   * @return 全部任务列表
   */
  @Override
  public List<YamlTask> findAll() {
    return new ArrayList<>(taskMap.values());
  }
}
