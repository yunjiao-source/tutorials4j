package tutorials4j.framework.assy.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import tutorials4j.framework.assy.schedule.TaskEvent.TaskEventType;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class DynamicTaskManager implements SchedulingConfigurer {
  private final Map<String, ScheduledTask> registeredTasks = new ConcurrentHashMap<>();
  private final Map<String, TaskDefinition> taskDefinitions = new ConcurrentHashMap<>();
  private ScheduledTaskRegistrar taskRegistrar;
  private final ApplicationEventPublisher eventPublisher;
  private final TaskExecutorResolver taskExecutorResolver;
  private final TaskStatisticsManager statsManager;

  public DynamicTaskManager(
      ApplicationEventPublisher eventPublisher,
      TaskExecutorResolver taskExecutorResolver,
      TaskStatisticsManager statsManager) {
    this.eventPublisher = eventPublisher;
    this.taskExecutorResolver = taskExecutorResolver;
    this.statsManager = statsManager;
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar registrar) {
    this.taskRegistrar = registrar;
    // 可选：设置线程池
    registrar.setScheduler(Executors.newScheduledThreadPool(10));
  }

  /** 加载任务（启动时调用，从 YAML 或 DB 加载） */
  public void loadTasks(List<TaskDefinition> definitions) {
    definitions.forEach(
        def -> {
          taskDefinitions.put(def.getTaskId(), def);
          if (def.getEnabled()) {
            registerTask(def);
          }
        });
  }

  /** 注册一个任务到调度器 */
  private void registerTask(TaskDefinition definition) {
    Runnable actualTask = taskExecutorResolver.resolve(definition);
    ScheduledRunnable runnable =
        new ScheduledRunnable(definition.getTaskId(), actualTask, eventPublisher, statsManager);
    Trigger trigger = new CronTrigger(definition.getCron());

    ScheduledTask scheduledTask =
        taskRegistrar.scheduleTriggerTask(new TriggerTask(runnable, trigger));
    registeredTasks.put(definition.getTaskId(), scheduledTask);

    eventPublisher.publishEvent(
        new TaskEvent(
            this, definition.getTaskId(), definition.getName(), TaskEventType.CREATED, null));
  }

  /** 添加新任务 */
  public void addTask(TaskDefinition definition) {
    taskDefinitions.put(definition.getTaskId(), definition);
    if (definition.getEnabled()) {
      registerTask(definition);
    }
    // 持久化到 DB（示例略）
  }

  /** 更新任务（支持修改 cron、状态等） */
  public void updateTask(TaskDefinition newDefinition) {
    String taskId = newDefinition.getTaskId();
    TaskDefinition old = taskDefinitions.get(taskId);
    if (old == null) return;

    // 取消旧调度
    cancelTask(taskId);

    // 更新定义
    taskDefinitions.put(taskId, newDefinition);

    // 如果新任务是启用状态，重新注册
    if (newDefinition.getEnabled()) {
      registerTask(newDefinition);
    }
    // 持久化
  }

  /** 删除任务 */
  public void removeTask(String taskId) {
    cancelTask(taskId);
    taskDefinitions.remove(taskId);
    statsManager.removeStatistics(taskId);
    eventPublisher.publishEvent(new TaskEvent(this, taskId, null, TaskEventType.DELETED, null));
    // 持久化删除
  }

  /** 暂停任务（设置 enabled=false，取消调度） */
  public void pauseTask(String taskId) {
    TaskDefinition def = taskDefinitions.get(taskId);
    if (def != null && def.getEnabled()) {
      def.setEnabled(false);
      cancelTask(taskId);
      eventPublisher.publishEvent(
          new TaskEvent(this, taskId, def.getName(), TaskEventType.PAUSED, null));
      updateTask(def); // 持久化
    }
  }

  /** 恢复任务 */
  public void resumeTask(String taskId) {
    TaskDefinition def = taskDefinitions.get(taskId);
    if (def != null && !def.getEnabled()) {
      def.setEnabled(true);
      registerTask(def);
      eventPublisher.publishEvent(
          new TaskEvent(this, taskId, def.getName(), TaskEventType.RESUMED, null));
      updateTask(def);
    }
  }

  private void cancelTask(String taskId) {
    ScheduledTask scheduledTask = registeredTasks.remove(taskId);
    if (scheduledTask != null) {
      scheduledTask.cancel();
    }
  }

  public List<TaskDefinition> getAllTasks() {
    return new ArrayList<>(taskDefinitions.values());
  }

  // 提供获取统计数据的方法（由 Controller 调用）
  public TaskStatistics getStatistics(String taskId) {
    return statsManager.getStatistics(taskId);
  }
}
