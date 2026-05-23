package tutorials4j.springboot3.scheduling.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.Data;

/**
 * 配置仓库接口
 *
 * @author Yun Jiao
 */
public interface ConfigRepository {

  /** 获取任务的cron表达式 */
  Optional<String> getCronExpression(String taskName);

  /** 获取所有启用的cron表达式 */
  Map<String, String> getAllCronExpressions();

  /** 更新cron表达式 */
  boolean updateCronExpression(String taskName, String cronExpression);

  /** 更新任务状态 */
  boolean updateTaskStatus(String taskName, boolean enabled);

  /** 添加配置变更监听器 */
  void addListener(Consumer<ConfigChangeEvent> listener);

  /** 移除配置变更监听器 */
  void removeListener(Consumer<ConfigChangeEvent> listener);

  /** 获取任务配置详情 */
  Optional<TaskConfig> getTaskConfig(String taskName);

  /** 重新加载配置 */
  void reload();

  /**
   * 配置变更事件
   *
   * @author yangyunjiao
   */
  @Data
  class ConfigChangeEvent {
    private final String taskName;
    private final ConfigChangeType changeType;
    private final long timestamp;

    public ConfigChangeEvent(String taskName, ConfigChangeType changeType) {
      this.taskName = taskName;
      this.changeType = changeType;
      this.timestamp = System.currentTimeMillis();
    }

    public String getTaskName() {
      return taskName;
    }

    public ConfigChangeType getChangeType() {
      return changeType;
    }

    public long getTimestamp() {
      return timestamp;
    }

    public enum ConfigChangeType {
      CREATED,
      UPDATED,
      DELETED,
      ENABLED,
      DISABLED
    }
  }

  /**
   * 任务配置
   *
   * @author yangyunjiao
   */
  @Data
  class TaskConfig {
    private final String taskName;
    private final String cronExpression;
    private final boolean enabled;
    private final String description;
    private final Map<String, Object> metadata;

    public TaskConfig(
        String taskName,
        String cronExpression,
        boolean enabled,
        String description,
        Map<String, Object> metadata) {
      this.taskName = taskName;
      this.cronExpression = cronExpression;
      this.enabled = enabled;
      this.description = description;
      this.metadata = metadata != null ? metadata : new HashMap<>();
    }
  }
}
