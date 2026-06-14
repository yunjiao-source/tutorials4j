package tutorials4j.framework.schedule.core.component;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.annotation.PreDestroy;
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
import tutorials4j.framework.schedule.core.bean.TaskCondition;
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
    task.isInvalid();

    String name = task.getName();
    if (!task.isEnabled()) {
      log.warn("忽略操作！ 任务未开启, taksName={}", name);
      return;
    }

    if (triggerTaskMap.containsKey(name)) {
      log.warn("忽略操作！ 任务已经存在, taksName={}", name);
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

    String taskName = task.getName();
    if (triggerTaskMap.containsKey(taskName)) {
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
    triggerTaskMap.put(taskName, container);
    createEvent(taskName);
  }

  public void cancelTask(String taskName) {
    if (isDestroy) {
      log.warn("Instance is destroyed, SKIP!!!");
      return;
    }

    if (!triggerTaskMap.containsKey(taskName)) {
      return;
    }

    doCancelTask(taskName);
    cancelEvent(taskName);
  }

  public List<ChangeStatusEventConsumer> getChangeStatusEventConsumers() {
    return Collections.unmodifiableList(this.consumers);
  }

  public TaskCondition getLastTaskCondition(String taskName) {
    ScheduledTaskData data = triggerTaskMap.get(taskName);
    if (data == null) {
      return null;
    }

    return data.runner().getLastTaskCondition();
  }

  public Collection<String> getTaskNames() {
    return Collections.unmodifiableSet(triggerTaskMap.keySet());
  }

  @PreDestroy
  public void destroy() {
    isDestroy = true;
    if (log.isDebugEnabled()) {
      log.debug("正在销毁所有计划任务，计划任务总数={}", triggerTaskMap.size());
    }
    List<String> taskNames = new ArrayList<>(triggerTaskMap.keySet());
    for (String taskName : taskNames) {
      doCancelTask(taskName);
    }
  }

  @Override
  public void configureTasks(@NotNull ScheduledTaskRegistrar taskRegistrar) {
    this.scheduledTaskRegistrar = taskRegistrar;
    initTasks();
  }

  private synchronized void doCancelTask(String taskName) {
    ScheduledTaskData container = triggerTaskMap.get(taskName);
    if (container == null) {
      return;
    }

    triggerTaskMap.remove(taskName);
    container.scheduledTask().cancel();
  }

  private void createEvent(String taskName) {
    notifyListeners(taskName, TaskStatusEnum.CREATED, null, null);
  }

  private void cancelEvent(String taskName) {
    notifyListeners(taskName, TaskStatusEnum.CANCELLED, null, null);
  }

  private void stopEvent(String taskName, String message) {
    doCancelTask(taskName);
    notifyListeners(taskName, TaskStatusEnum.STOPPED, message, null);
  }

  private void failureEvent(String taskName, Throwable throwable) {
    notifyListeners(taskName, TaskStatusEnum.EXCEPTION, null, throwable);
  }

  private void completeEvent(String taskName, TaskCondition taskCondition) {
    notifyListeners(taskName, TaskStatusEnum.COMPLETED, null, null);
  }

  private void startEvent(String taskName) {
    notifyListeners(taskName, TaskStatusEnum.STARTED, null, null);
  }

  private void notifyListeners(
      String taskName, TaskStatusEnum taskStatus, String message, Throwable throwable) {
    ChangeStatusEvent event =
        ChangeStatusEvent.builder()
            .timestamp(System.nanoTime())
            .taskName(taskName)
            .lastTaskCondition(getLastTaskCondition(taskName))
            .taskStatus(taskStatus)
            .message(message)
            .throwable(throwable)
            .build();
    consumers.forEach(consumer -> consumer.consumer(event));
  }
}
