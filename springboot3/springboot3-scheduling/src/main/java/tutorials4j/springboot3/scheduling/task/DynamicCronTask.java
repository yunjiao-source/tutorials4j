package tutorials4j.springboot3.scheduling.task;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tutorials4j.springboot3.scheduling.repository.ConfigRepository;

/**
 * 动态CRON表达式配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class DynamicCronTask {
  private final ConfigRepository configRepository;
  private final TaskScheduler taskScheduler;

  @Getter private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

  public DynamicCronTask(
      // @Qualifier("propertiesConfigRepository")
      @Qualifier("yamlConfigRepository") ConfigRepository configRepository,
      TaskScheduler taskScheduler) {
    this.configRepository = configRepository;
    this.taskScheduler = taskScheduler;
  }

  @PostConstruct
  public void init() {
    // 初始化加载所有任务
    initializeTasks();

    // 监听配置变更
    configRepository.addListener(this::handleConfigChange);
  }

  private void initializeTasks() {
    configRepository
        .getAllCronExpressions()
        .forEach(
            (taskName, cron) -> {
              scheduleTask(taskName, cron);
            });
    log.info("Initialized {} scheduled tasks", scheduledTasks.size());
  }

  private void scheduleTask(String taskName, String cronExpression) {
    if (StringUtils.hasText(cronExpression)) {
      try {
        CronTrigger trigger = new CronTrigger(cronExpression);
        ScheduledFuture<?> future = taskScheduler.schedule(() -> executeTask(taskName), trigger);
        scheduledTasks.put(taskName, future);
        log.info("Scheduled task: {} with cron: {}", taskName, cronExpression);
      } catch (Exception e) {
        log.error("Invalid cron expression for task {}: {}", taskName, cronExpression, e);
      }
    }
  }

  private void handleConfigChange(ConfigRepository.ConfigChangeEvent event) {
    String taskName = event.getTaskName();

    // 取消现有任务
    ScheduledFuture<?> existingTask = scheduledTasks.remove(taskName);
    if (existingTask != null) {
      existingTask.cancel(false);
      log.info("Cancelled existing task: {}", taskName);
    }

    // 获取新的配置
    Optional<String> newCron = configRepository.getCronExpression(taskName);

    // 重新调度任务
    if (newCron.isPresent()) {
      scheduleTask(taskName, newCron.get());
    } else {
      log.info("Task {} is disabled or removed", taskName);
    }
  }

  private void executeTask(String taskName) {
    try {
      log.info("Executing task: {}", taskName);
      // 根据任务名称执行不同的业务逻辑
      // 可以从configRepository获取更多配置信息
      configRepository
          .getTaskConfig(taskName)
          .ifPresent(
              config -> {
                // 使用配置信息
                log.debug("Task config: {}", config);
              });
    } catch (Exception e) {
      log.error("Error executing task: {}", taskName, e);
    }
  }

  @PreDestroy
  public void stop() {
    scheduledTasks.forEach(
        (k, v) -> {
          log.info("Stop taks : {}", k);
          v.cancel(false);
        });
  }
}
