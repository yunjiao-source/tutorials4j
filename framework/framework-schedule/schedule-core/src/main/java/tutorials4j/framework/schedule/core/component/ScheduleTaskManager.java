package tutorials4j.framework.schedule.core.component;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.annotation.PreDestroy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.util.Assert;
import tutorials4j.framework.schedule.core.bean.ChangeStatusEvent;
import tutorials4j.framework.schedule.core.bean.ScheduledTaskData;
import tutorials4j.framework.schedule.core.bean.Task;
import tutorials4j.framework.schedule.core.bean.TaskRunData;
import tutorials4j.framework.schedule.core.bean.TaskRunner;
import tutorials4j.framework.schedule.core.bean.TaskStatusEnum;
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
  private final List<ChangeStatusEventConsumer> consumers;

  private ScheduledTaskRegistrar scheduledTaskRegistrar;
  private final ConcurrentMap<String, ScheduledTaskData> triggerTaskMap = new ConcurrentHashMap<>();

  private volatile boolean isDestroy = false;

  private void initTasks() {
    taskRepository.findAll().forEach(this::addTask);
  }

  public void addTask(Task task) {
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
    if (isDestroy) {
      log.warn("Instance is destroyed, SKIP!!!");
      return;
    }

    Assert.notNull(scheduledTaskRegistrar, "task must not be null");

    String taskCode = task.getTaskCode();
    if (triggerTaskMap.containsKey(taskCode)) {
      return;
    }

    String classSimpleName = task.getClassSimpleName();
    TaskRunner taskRunner = SpringUtil.getBean(classSimpleName, TaskRunner.class);
    if (taskRunner == null) {
      log.warn("Spring容器中找不到任务Bean，或者任务类未实现TaskRunner接口， className={}", classSimpleName);
      return;
    }

    RunnableDecorator runnableDecorator = new RunnableDecorator(task, taskRunner);
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
    createEvent(taskCode);
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

    doCancelTask(taskCode);
    cancelEvent(taskCode);
  }

  public List<ChangeStatusEventConsumer> getChangeStatusEventConsumers() {
    return Collections.unmodifiableList(this.consumers);
  }

  public TaskRunData getLastTaskRunData(String taskCode) {
    ScheduledTaskData data = triggerTaskMap.get(taskCode);
    if (data == null) {
      return null;
    }

    return data.runner().getLastTaskRunData();
  }

  public Collection<String> getTaskCodes() {
    return Collections.unmodifiableSet(triggerTaskMap.keySet());
  }

  @PreDestroy
  public void destroy() {
    isDestroy = true;
    if (log.isDebugEnabled()) {
      log.debug("正在销毁所有计划任务，计划任务总数={}", triggerTaskMap.size());
    }
    List<String> taskCodes = new ArrayList<>(triggerTaskMap.keySet());
    for (String taskCode : taskCodes) {
      doCancelTask(taskCode);
    }
  }

  @Override
  public void configureTasks(@NotNull ScheduledTaskRegistrar taskRegistrar) {
    this.scheduledTaskRegistrar = taskRegistrar;
    initTasks();
  }

  private synchronized void doCancelTask(String taskCode) {
    ScheduledTaskData container = triggerTaskMap.get(taskCode);
    if (container == null) {
      return;
    }

    triggerTaskMap.remove(taskCode);
    container.scheduledTask().cancel();
  }

  private void createEvent(String taskCode) {
    notifyListeners(taskCode, TaskStatusEnum.CREATED, null, null);
  }

  private void cancelEvent(String taskCode) {
    notifyListeners(taskCode, TaskStatusEnum.CANCELLED, null, null);
  }

  private void stopEvent(String taskCode, String message) {
    doCancelTask(taskCode);
    notifyListeners(taskCode, TaskStatusEnum.STOPPED, message, null);
  }

  private void failureEvent(String taskCode, Throwable throwable) {
    notifyListeners(taskCode, TaskStatusEnum.EXCEPTION, null, throwable);
  }

  private void completeEvent(String taskCode, TaskRunData taskRunData) {
    notifyListeners(taskCode, TaskStatusEnum.COMPLETED, null, null);
  }

  private void startEvent(String taskCode) {
    notifyListeners(taskCode, TaskStatusEnum.STARTED, null, null);
  }

  private void notifyListeners(
      String taskCode, TaskStatusEnum taskStatus, String message, Throwable throwable) {
    ChangeStatusEvent event =
        ChangeStatusEvent.builder()
            .timestamp(Instant.now())
            .taskCode(taskCode)
            .lastTaskRunData(getLastTaskRunData(taskCode))
            .taskStatus(taskStatus)
            .message(message)
            .throwable(throwable)
            .build();

    try {
      consumers.forEach(consumer -> consumer.consumer(event));
    } catch (Exception e) {
      log.error("执行任务状态监听异常，taskCode={}, taskStatus={}", taskCode, taskStatus, e);
    }
  }
}
