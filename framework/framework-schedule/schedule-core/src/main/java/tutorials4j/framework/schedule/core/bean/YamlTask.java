package tutorials4j.framework.schedule.core.bean;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import org.springframework.util.Assert;
import tutorials4j.framework.schedule.core.properties.TaskExecutionOptions;
import tutorials4j.framework.schedule.core.properties.TaskOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class YamlTask implements Task {
  private String name;
  private String classSimpleName;
  private String cron;
  private boolean enabled;
  private String description;
  private Map<String, String> metadata;

  private Duration initialDelay;
  private Integer maxFailureCount;
  private Integer maxExecutionCount;
  private Instant dueDate;

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setClassSimpleName(String classSimpleName) {
    this.classSimpleName = classSimpleName;
  }

  @Override
  public String getClassSimpleName() {
    return classSimpleName;
  }

  @Override
  public void setCron(String cron) {
    this.cron = cron;
  }

  @Override
  public String getCron() {
    return cron;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setMetadata(Map<String, String> metadata) {
    Assert.notNull(metadata, "metadata must not be null");
    this.metadata = metadata;
  }

  @Override
  public Map<String, String> getMetadata() {
    return Collections.unmodifiableMap(metadata);
  }

  @Override
  public void setInitialDelay(Duration initialDelay) {
    this.initialDelay = initialDelay;
  }

  @Override
  public Duration getInitialDelay() {
    return initialDelay;
  }

  @Override
  public void setMaxFailureCount(Integer maxFailureCount) {
    this.maxFailureCount = maxFailureCount;
  }

  @Override
  public Integer getMaxFailureCount() {
    return maxFailureCount;
  }

  @Override
  public void setMaxExecutionCount(Integer maxExecutionCount) {
    this.maxExecutionCount = maxExecutionCount;
  }

  @Override
  public Integer getMaxExecutionCount() {
    return maxExecutionCount;
  }

  @Override
  public void setDueDate(Instant dueDate) {
    this.dueDate = dueDate;
  }

  @Override
  public Instant getDueDate() {
    return dueDate;
  }

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
