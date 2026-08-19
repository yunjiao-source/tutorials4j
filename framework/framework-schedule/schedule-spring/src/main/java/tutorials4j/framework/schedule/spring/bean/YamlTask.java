package tutorials4j.framework.schedule.spring.bean;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import org.springframework.util.Assert;
import tutorials4j.framework.schedule.core.bean.Task;
import tutorials4j.framework.schedule.spring.properties.TaskExecutionOptions;
import tutorials4j.framework.schedule.spring.properties.TaskOptions;

/**
 * 基于 YAML 配置的任务实现。
 *
 * <p>实现 {@link Task} 接口，将从 YAML 配置中解析出的任务信息（如任务编码、cron 表达式、启用开关、 元数据与执行选项等）封装为可被调度器使用的任务对象。
 *
 * @author Yun Jiao
 */
public class YamlTask implements Task {
  private String taskCode;
  private String classSimpleName;
  private String cron;
  private boolean enabled;
  private String description;
  private Map<String, String> metadata;

  private Duration initialDelay;
  private Integer maxFailureCount;
  private Integer maxExecutionCount;
  private Instant dueDate;

  /** 设置任务编码。 */
  @Override
  public void setTaskCode(String taskCode) {
    this.taskCode = taskCode;
  }

  /** 获取任务编码。 */
  @Override
  public String getTaskCode() {
    return taskCode;
  }

  /** 设置任务类简单名。 */
  @Override
  public void setClassSimpleName(String classSimpleName) {
    this.classSimpleName = classSimpleName;
  }

  /** 获取任务类简单名。 */
  @Override
  public String getClassSimpleName() {
    return classSimpleName;
  }

  /** 设置 cron 表达式。 */
  @Override
  public void setCron(String cron) {
    this.cron = cron;
  }

  /** 获取 cron 表达式。 */
  @Override
  public String getCron() {
    return cron;
  }

  /** 设置是否启用。 */
  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /** 获取是否启用。 */
  @Override
  public boolean isEnabled() {
    return enabled;
  }

  /** 设置任务描述。 */
  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  /** 获取任务描述。 */
  @Override
  public String getDescription() {
    return description;
  }

  /** 设置任务元数据，元数据不能为 {@code null}。 */
  @Override
  public void setMetadata(Map<String, String> metadata) {
    Assert.notNull(metadata, "metadata must not be null");
    this.metadata = metadata;
  }

  /** 返回任务元数据的不可变视图。 */
  @Override
  public Map<String, String> getMetadata() {
    return Collections.unmodifiableMap(metadata);
  }

  /** 设置首次执行延时。 */
  @Override
  public void setInitialDelay(Duration initialDelay) {
    this.initialDelay = initialDelay;
  }

  /** 获取首次执行延时。 */
  @Override
  public Duration getInitialDelay() {
    return initialDelay;
  }

  /** 设置最大失败次数。 */
  @Override
  public void setMaxFailureCount(Integer maxFailureCount) {
    this.maxFailureCount = maxFailureCount;
  }

  /** 获取最大失败次数。 */
  @Override
  public Integer getMaxFailureCount() {
    return maxFailureCount;
  }

  /** 设置最大执行次数。 */
  @Override
  public void setMaxExecutionCount(Integer maxExecutionCount) {
    this.maxExecutionCount = maxExecutionCount;
  }

  /** 获取最大执行次数。 */
  @Override
  public Integer getMaxExecutionCount() {
    return maxExecutionCount;
  }

  /** 设置任务结束日期。 */
  @Override
  public void setDueDate(Instant dueDate) {
    this.dueDate = dueDate;
  }

  /** 获取任务结束日期。 */
  @Override
  public Instant getDueDate() {
    return dueDate;
  }

  /**
   * 根据任务配置选项创建任务实例。
   *
   * @param taskOptions 任务配置选项，不能为 {@code null}
   * @return 构建完成的 {@link YamlTask} 实例
   */
  public static YamlTask of(TaskOptions taskOptions) {
    Assert.notNull(taskOptions, "taskOptions must not be null");
    YamlTask yamlTask = new YamlTask();
    yamlTask.setClassSimpleName(taskOptions.getClassSimpleName());
    yamlTask.setCron(taskOptions.getCron());
    yamlTask.setEnabled(taskOptions.isEnabled());
    yamlTask.setDescription(taskOptions.getDescription());
    yamlTask.setMetadata(Collections.unmodifiableMap(taskOptions.getMetadata()));

    TaskExecutionOptions executionOptions = taskOptions.getExecution();
    yamlTask.setInitialDelay(executionOptions.getInitialDelay());
    yamlTask.setMaxFailureCount(executionOptions.getMaxFailureCount());
    yamlTask.setMaxExecutionCount(executionOptions.getMaxExecutionCount());
    yamlTask.setDueDate(executionOptions.getDueDate());
    return yamlTask;
  }
}
