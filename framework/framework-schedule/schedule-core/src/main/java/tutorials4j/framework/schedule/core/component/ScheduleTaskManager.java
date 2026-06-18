package tutorials4j.framework.schedule.core.component;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.annotation.PreDestroy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.util.Assert;
import tutorials4j.framework.schedule.core.bean.ChangeStatusEvent;
import tutorials4j.framework.schedule.core.bean.ScheduledTaskData;
import tutorials4j.framework.schedule.core.bean.Task;
import tutorials4j.framework.schedule.core.bean.TaskRunner;
import tutorials4j.framework.schedule.core.bean.TaskRuntimeData;
import tutorials4j.framework.schedule.core.bean.TaskStatusEnum;
import tutorials4j.framework.schedule.core.properties.ScheduleProperties;
import tutorials4j.framework.schedule.core.repository.TaskRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class ScheduleTaskManager implements SchedulingConfigurer {
  private final TaskRepository<?> taskRepository;
  private final EventConsumerContainer consumers;
  private final ScheduleProperties properties;

  private ScheduledTaskRegistrar scheduledTaskRegistrar;
  private final ConcurrentMap<String, ScheduledTaskData> triggerTaskMap = new ConcurrentHashMap<>();

  @Getter private Map<String, TaskRuntimeData> lastTaskRuntimeDataMap = new ConcurrentHashMap<>();

  private volatile boolean isDestroy = false;

  private void createTasks() {
    if (!properties.isAllTaskAutoStartOnBoot()) {
      log.warn("配置属性: allTaskAutoStartOnBoot=false, 忽略创建所有定时任务");
      return;
    }
    taskRepository.findAll().forEach(this::createTask);
  }

  public void createTask(Task task) {
    Assert.notNull(task, "task must not be null");
    task.assertValid();

    String taskCode = task.getTaskCode();
    if (!task.isEnabled()) {
      log.warn("忽略操作！ 任务未开启, taskCode={}", taskCode);
      return;
    }

    if (triggerTaskMap.containsKey(taskCode)) {
      log.warn("忽略操作！ 任务已经存在, taskCode={}", taskCode);
      return;
    }

    doAddTask(task);
  }

  private synchronized void doAddTask(Task task) {
    if (log.isDebugEnabled()) {
      log.debug("[SCHEDULE-CORE] Add Schedule Task. taskCode={}", task.getTaskCode());
    }

    if (isDestroy) {
      log.warn("Instance is destroyed, SKIP!!!");
      return;
    }
    Assert.notNull(scheduledTaskRegistrar, "scheduledTaskRegistrar must not be null");

    String taskCode = task.getTaskCode();
    if (triggerTaskMap.containsKey(taskCode)) {
      return;
    }

    String classSimpleName = task.getClassSimpleName();
    TaskRunner taskRunner = null;
    try {
      taskRunner = SpringUtil.getBean(classSimpleName, TaskRunner.class);
    } catch (BeansException e) {
      log.error("获取bean异常, classSimpleName = {}, errorMessage={}", classSimpleName, e.getMessage());
    }

    if (taskRunner == null) {
      log.warn("Spring容器中找不到任务Bean，或者任务类未实现TaskRunner接口， className={}", classSimpleName);
      return;
    }

    RunnableDecorator runnableDecorator =
        new RunnableDecorator(task, taskRunner, properties.getDefaultExecution());
    runnableDecorator
        .onStart(this::startEvent)
        .onComplete(this::completeEvent)
        .onFailure(this::failureEvent)
        .onStop(this::stopEvent);
    TriggerTask triggerTask = new TriggerTask(runnableDecorator, runnableDecorator);
    ScheduledTask scheduledTask = scheduledTaskRegistrar.scheduleTriggerTask(triggerTask);

    ScheduledTaskData container =
        ScheduledTaskData.builder()
            .scheduledTask(scheduledTask)
            .runner(runnableDecorator)
            .triggerTask(triggerTask)
            .build();
    triggerTaskMap.put(taskCode, container);
    createEvent(
        runnableDecorator.buildTaskRuntimeData().taskStatus(TaskStatusEnum.CREATED).build());
  }

  public void cancelTask(String taskCode) {
    if (isDestroy) {
      log.warn("实例已经销毁，忽略此次操作");
      return;
    }

    if (!triggerTaskMap.containsKey(taskCode)) {
      log.warn("任务不存在，无法取消，忽略此次操作；taskCode={}", taskCode);
      return;
    }

    ScheduledTaskData scheduledTaskData = doCancelTask(taskCode);
    if (scheduledTaskData != null) {
      cancelEvent(
          scheduledTaskData
              .runner()
              .buildTaskRuntimeData()
              .taskStatus(TaskStatusEnum.CANCELLED)
              .build());
    }
  }

  public Task getTask(String taskCode) {
    ScheduledTaskData data = triggerTaskMap.get(taskCode);
    if (data == null) {
      return null;
    }

    return data.runner().getTask();
  }

  public TaskRuntimeData getLastTaskRuntimeData(String taskCode) {
    return lastTaskRuntimeDataMap.get(taskCode);
  }

  public boolean isTaskRunning(String taskCode) {
    return triggerTaskMap.containsKey(taskCode);
  }

  public Collection<String> getTaskCodes() {
    return Collections.unmodifiableSet(triggerTaskMap.keySet());
  }

  @PreDestroy
  public void destroy() {
    isDestroy = true;
    if (log.isDebugEnabled()) {
      log.debug("正在销毁所有计划任务，任务总数={}", triggerTaskMap.size());
    }
    List<String> taskCodes = new ArrayList<>(triggerTaskMap.keySet());
    for (String taskCode : taskCodes) {
      doCancelTask(taskCode);
    }
  }

  @Override
  public void configureTasks(@NotNull ScheduledTaskRegistrar taskRegistrar) {
    this.scheduledTaskRegistrar = taskRegistrar;
    createTasks();
  }

  private synchronized ScheduledTaskData doCancelTask(String taskCode) {
    ScheduledTaskData container = triggerTaskMap.get(taskCode);
    if (container == null) {
      return null;
    }

    triggerTaskMap.remove(taskCode);
    container.scheduledTask().cancel();
    return container;
  }

  private void createEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    notifyConsumers(data);
  }

  private void cancelEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    notifyConsumers(data);
  }

  private void stopEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    doCancelTask(data.taskCode());
    notifyConsumers(data);
  }

  private void failureEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    notifyConsumers(data);
  }

  private void completeEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    notifyConsumers(data);
  }

  private void startEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    notifyConsumers(data);
  }

  private void notifyConsumers(TaskRuntimeData data) {
    ChangeStatusEvent event =
        ChangeStatusEvent.builder().timestamp(Instant.now()).taskRuntimeData(data).build();

    this.consumers.notifyConsumers(event);
  }
}
