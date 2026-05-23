package tutorials4j.springboot3.scheduling.repository;

import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * YAML配置仓库实现 从 application.yml 中的 scheduled.tasks 配置读取
 *
 * @author Yun Jiao
 */
@Component("yamlConfigRepository")
@ConfigurationProperties(prefix = "scheduled.tasks")
@Slf4j
public class YamlConfigRepository implements ConfigRepository {

  private final Map<String, TaskConfig> tasks = new ConcurrentHashMap<>();
  private final List<Consumer<ConfigChangeEvent>> listeners = new CopyOnWriteArrayList<>();
  private final ApplicationEventPublisher eventPublisher;

  // 配置类，对应 yaml 结构
  @Data
  public static class TaskProperties {
    private String cron;
    private boolean enabled = true;
    private String description;
    private Map<String, Object> metadata = new HashMap<>();
    private Map<String, Object> retry = new HashMap<>();
    private Map<String, Object> alert = new HashMap<>();
  }

  @Getter @Setter private Map<String, TaskProperties> properties = new HashMap<>();

  public YamlConfigRepository(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @PostConstruct
  public void init() {
    loadFromProperties();
    log.info("YAML ConfigRepository initialized with {} tasks", tasks.size());
  }

  private void loadFromProperties() {
    tasks.clear();

    properties.forEach(
        (taskName, properties) -> {
          if (properties != null) {
            TaskConfig config =
                new TaskConfig(
                    taskName,
                    properties.getCron(),
                    properties.isEnabled(),
                    properties.getDescription(),
                    buildMetadata(properties));
            tasks.put(taskName, config);
            log.debug("Loaded task config: {} -> {}", taskName, properties.getCron());
          }
        });
  }

  private Map<String, Object> buildMetadata(TaskProperties properties) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("source", "yaml");
    metadata.put("loadedAt", new Date());

    if (properties.getMetadata() != null) {
      metadata.putAll(properties.getMetadata());
    }

    if (properties.getRetry() != null && !properties.getRetry().isEmpty()) {
      metadata.put("retry", properties.getRetry());
    }

    if (properties.getAlert() != null && !properties.getAlert().isEmpty()) {
      metadata.put("alert", properties.getAlert());
    }

    return metadata;
  }

  @Override
  public Optional<String> getCronExpression(String taskName) {
    TaskConfig config = tasks.get(taskName);
    return Optional.ofNullable(config)
        .filter(TaskConfig::isEnabled)
        .map(TaskConfig::getCronExpression);
  }

  @Override
  public Map<String, String> getAllCronExpressions() {
    return tasks.values().stream()
        .filter(TaskConfig::isEnabled)
        .filter(config -> config.getCronExpression() != null)
        .collect(Collectors.toMap(TaskConfig::getTaskName, TaskConfig::getCronExpression));
  }

  @Override
  public Optional<TaskConfig> getTaskConfig(String taskName) {
    return Optional.ofNullable(tasks.get(taskName));
  }

  @Override
  public boolean updateCronExpression(String taskName, String cronExpression) {
    TaskConfig existing = tasks.get(taskName);
    if (existing != null) {
      TaskConfig updated =
          new TaskConfig(
              taskName,
              cronExpression,
              existing.isEnabled(),
              existing.getDescription(),
              existing.getMetadata());
      tasks.put(taskName, updated);

      notifyListeners(new ConfigChangeEvent(taskName, ConfigChangeEvent.ConfigChangeType.UPDATED));

      log.info("Updated cron expression for task {} to {}", taskName, cronExpression);
      return true;
    }

    log.warn("Task {} not found for cron update", taskName);
    return false;
  }

  @Override
  public boolean updateTaskStatus(String taskName, boolean enabled) {
    TaskConfig existing = tasks.get(taskName);
    if (existing != null) {
      TaskConfig updated =
          new TaskConfig(
              taskName,
              existing.getCronExpression(),
              enabled,
              existing.getDescription(),
              existing.getMetadata());
      tasks.put(taskName, updated);

      notifyListeners(
          new ConfigChangeEvent(
              taskName,
              enabled
                  ? ConfigChangeEvent.ConfigChangeType.ENABLED
                  : ConfigChangeEvent.ConfigChangeType.DISABLED));

      log.info("Updated task {} status to enabled={}", taskName, enabled);
      return true;
    }

    log.warn("Task {} not found for status update", taskName);
    return false;
  }

  @Override
  public void addListener(Consumer<ConfigChangeEvent> listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(Consumer<ConfigChangeEvent> listener) {
    listeners.remove(listener);
  }

  @Override
  public void reload() {
    loadFromProperties();
    log.info("Reloaded YAML configurations, total {} tasks", tasks.size());

    // 通知所有任务配置已重新加载
    tasks
        .keySet()
        .forEach(
            taskName -> {
              notifyListeners(
                  new ConfigChangeEvent(taskName, ConfigChangeEvent.ConfigChangeType.UPDATED));
            });
  }

  private void notifyListeners(ConfigChangeEvent event) {
    listeners.forEach(
        listener -> {
          try {
            listener.accept(event);
          } catch (Exception e) {
            log.error("Error notifying listener for event: {}", event.getTaskName(), e);
          }
        });
  }

  /** 定时检查外部配置变更 */
  //    @Scheduled(fixedDelay = 30000) // 每30秒检查一次
  //    public void checkForExternalChanges() {
  //        // YAML配置通常在启动时加载，这里可以扩展为检查外部配置源
  //        // 例如从配置中心获取更新
  //    }

  // Setter for Spring Boot configuration binding
  public void setTasks(Map<String, TaskProperties> tasks) {
    this.properties = tasks != null ? tasks : new HashMap<>();
  }
}
