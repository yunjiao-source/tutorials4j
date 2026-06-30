package tutorials4j.framework.schedule.spring.component;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.annotation.PreDestroy;
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
import tutorials4j.framework.schedule.core.bean.Task;
import tutorials4j.framework.schedule.core.bean.TaskRunner;
import tutorials4j.framework.schedule.core.exception.ScheduleErrorCode;
import tutorials4j.framework.schedule.spring.bean.RunnableDecorator;
import tutorials4j.framework.schedule.spring.bean.ScheduledTaskData;
import tutorials4j.framework.schedule.spring.bean.TaskRuntimeData;
import tutorials4j.framework.schedule.spring.bean.TaskStatusEnum;
import tutorials4j.framework.schedule.spring.handler.TaskRuntimeDataHandler;
import tutorials4j.framework.schedule.spring.properties.SpringScheduleProperties;
import tutorials4j.framework.schedule.spring.repository.TaskRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class ScheduleTaskManager implements SchedulingConfigurer {
  private final TaskRepository<?> taskRepository;
  private final List<TaskRuntimeDataHandler> taskRuntimeDataHandlers;
  private final SpringScheduleProperties properties;

  private ScheduledTaskRegistrar scheduledTaskRegistrar;
  private final ConcurrentMap<String, ScheduledTaskData> triggerTaskMap = new ConcurrentHashMap<>();

  @Getter private Map<String, TaskRuntimeData> lastTaskRuntimeDataMap = new ConcurrentHashMap<>();

  private volatile boolean isDestroy = false;

  private void createTasks() {
    if (!properties.isAllTaskAutoStartOnBoot()) {
      log.warn("忽略创建所有定时任务，因为配置属性allTaskAutoStartOnBoot=false");
      return;
    }
    taskRepository.findAll().forEach(this::createTask);
  }

  public void createTask(Task task) {
    Assert.notNull(task, "task must not be null");
    task.assertValid();
    Assert.notNull(scheduledTaskRegistrar, "scheduledTaskRegistrar must not be null");

    String taskCode = task.getTaskCode();
    if (!task.isEnabled()) {
      // TODO EXCEPTION
      return;
    }

    if (triggerTaskMap.containsKey(taskCode)) {
      // TODO EXCEPTION
      return;
    }

    doAddTask(task);
  }

  private synchronized void doAddTask(Task task) {
    if (log.isDebugEnabled()) {
      log.debug("添加定时任务， taskCode={}", task.getTaskCode());
    }

    if (isDestroy) {
      log.warn("实例已经被销毁");
      return;
    }

    String taskCode = task.getTaskCode();
    if (triggerTaskMap.containsKey(taskCode)) {
      return;
    }

    String classSimpleName = task.getClassSimpleName();
    TaskRunner taskRunner = null;
    try {
      taskRunner = SpringUtil.getBean(classSimpleName, TaskRunner.class);
    } catch (BeansException e) {
      throw ScheduleErrorCode.SCHEDULE_JOB_BEAN_NOT_EXIST
          .throwed(e)
          .param("class", classSimpleName);
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
      log.warn("实例已经销毁，无法取消，taskCode={}", taskCode);
      return;
    }

    if (!triggerTaskMap.containsKey(taskCode)) {
      log.warn("任务不存在，无法取消，taskCode={}", taskCode);
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
      log.debug("正在停止所有计划任务，总数={}", triggerTaskMap.size());
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
    notifyTaskRuntimeDataHandlers(data);
  }

  private void cancelEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    notifyTaskRuntimeDataHandlers(data);
  }

  private void stopEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    doCancelTask(data.taskCode());
    notifyTaskRuntimeDataHandlers(data);
  }

  private void failureEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    notifyTaskRuntimeDataHandlers(data);
  }

  private void completeEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    notifyTaskRuntimeDataHandlers(data);
  }

  private void startEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    notifyTaskRuntimeDataHandlers(data);
  }

  private void notifyTaskRuntimeDataHandlers(TaskRuntimeData data) {
    if (taskRuntimeDataHandlers == null || taskRuntimeDataHandlers.isEmpty()) {
      log.warn(
          "没有注册任务事件处理器'{}'，TaskRuntimeData={}", TaskRuntimeDataHandler.class.getSimpleName(), data);
      return;
    }

    try {
      for (TaskRuntimeDataHandler taskRuntimeDataHandler : taskRuntimeDataHandlers) {
        taskRuntimeDataHandler.handle(data);
      }
    } catch (Exception e) {
      log.error("处理任务事件异常", e);
    }
  }
}
