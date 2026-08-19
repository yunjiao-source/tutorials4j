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
 * 定时任务管理器。
 *
 * <p>实现 {@link SchedulingConfigurer}，负责将 {@link TaskRepository} 中的任务注册到 {@link
 * ScheduledTaskRegistrar}，并管理任务的创建、取消、查询以及运行数据的记录与分发。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class ScheduleTaskManager implements SchedulingConfigurer {
  /** 任务数据仓库，用于获取全部任务。 */
  private final TaskRepository<?> taskRepository;

  /** 任务运行数据处理器列表，用于分发任务事件。 */
  private final List<TaskRuntimeDataHandler> taskRuntimeDataHandlers;

  /** Spring 定时任务配置属性。 */
  private final SpringScheduleProperties properties;

  /** 任务调度注册器，由 {@link #configureTasks} 注入。 */
  private ScheduledTaskRegistrar scheduledTaskRegistrar;

  /** 已注册的任务编码到任务调度数据的映射。 */
  private final ConcurrentMap<String, ScheduledTaskData> triggerTaskMap = new ConcurrentHashMap<>();

  /** 最近一次的任务运行数据映射（键为任务编码）。 */
  @Getter private Map<String, TaskRuntimeData> lastTaskRuntimeDataMap = new ConcurrentHashMap<>();

  /** 实例是否已被销毁的标记。 */
  private volatile boolean isDestroy = false;

  /** 启动时创建全部定时任务（受 {@code allTaskAutoStartOnBoot} 配置控制）。 */
  private void createTasks() {
    if (!properties.isAllTaskAutoStartOnBoot()) {
      log.warn("忽略创建所有定时任务，因为配置属性allTaskAutoStartOnBoot=false");
      return;
    }
    taskRepository.findAll().forEach(this::createTask);
  }

  /**
   * 创建并注册一个定时任务。
   *
   * <p>任务必须有效且已启用；已注册的任务会被忽略。
   *
   * @param task 待创建的任务
   */
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

  /**
   * 实际添加定时任务：获取任务执行器 Bean，包装运行事件回调并注册到调度器。
   *
   * @param task 待添加的任务
   */
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

  /**
   * 取消指定编码的定时任务。
   *
   * @param taskCode 任务编码
   */
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

  /**
   * 获取指定编码的任务对象。
   *
   * @param taskCode 任务编码
   * @return 运行中的任务对象；任务未注册时返回 {@code null}
   */
  public Task getTask(String taskCode) {
    ScheduledTaskData data = triggerTaskMap.get(taskCode);
    if (data == null) {
      return null;
    }

    return data.runner().getTask();
  }

  /**
   * 获取指定编码任务的最近一次运行数据。
   *
   * @param taskCode 任务编码
   * @return 最近一次运行数据；尚无运行记录时返回 {@code null}
   */
  public TaskRuntimeData getLastTaskRuntimeData(String taskCode) {
    return lastTaskRuntimeDataMap.get(taskCode);
  }

  /**
   * 判断指定编码的任务是否正在运行（已注册）。
   *
   * @param taskCode 任务编码
   * @return 正在运行返回 {@code true}
   */
  public boolean isTaskRunning(String taskCode) {
    return triggerTaskMap.containsKey(taskCode);
  }

  /**
   * 获取全部已注册任务编码的只读集合。
   *
   * @return 任务编码集合
   */
  public Collection<String> getTaskCodes() {
    return Collections.unmodifiableSet(triggerTaskMap.keySet());
  }

  /** 销毁管理器：停止并取消全部已注册的定时任务。 */
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

  /**
   * 由 Spring 调度框架回调，保存注册器并创建启动任务。
   *
   * @param taskRegistrar 任务注册器
   */
  @Override
  public void configureTasks(@NotNull ScheduledTaskRegistrar taskRegistrar) {
    this.scheduledTaskRegistrar = taskRegistrar;
    createTasks();
  }

  /**
   * 实际取消任务并从注册表中移除。
   *
   * @param taskCode 任务编码
   * @return 被取消的任务调度数据；任务不存在时返回 {@code null}
   */
  private synchronized ScheduledTaskData doCancelTask(String taskCode) {
    ScheduledTaskData container = triggerTaskMap.get(taskCode);
    if (container == null) {
      return null;
    }

    triggerTaskMap.remove(taskCode);
    container.scheduledTask().cancel();
    return container;
  }

  /**
   * 记录任务创建事件并通知处理器。
   *
   * @param data 任务运行数据
   */
  private void createEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    notifyTaskRuntimeDataHandlers(data);
  }

  /**
   * 记录任务取消事件并通知处理器。
   *
   * @param data 任务运行数据
   */
  private void cancelEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    notifyTaskRuntimeDataHandlers(data);
  }

  /**
   * 记录任务停止事件、取消任务并通知处理器。
   *
   * @param data 任务运行数据
   */
  private void stopEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    doCancelTask(data.taskCode());
    notifyTaskRuntimeDataHandlers(data);
  }

  /**
   * 记录任务失败事件并通知处理器。
   *
   * @param data 任务运行数据
   */
  private void failureEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    notifyTaskRuntimeDataHandlers(data);
  }

  /**
   * 记录任务完成事件并通知处理器。
   *
   * @param data 任务运行数据
   */
  private void completeEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    notifyTaskRuntimeDataHandlers(data);
  }

  /**
   * 记录任务启动事件并通知处理器。
   *
   * @param data 任务运行数据
   */
  private void startEvent(TaskRuntimeData data) {
    lastTaskRuntimeDataMap.put(data.taskCode(), data);
    notifyTaskRuntimeDataHandlers(data);
  }

  /**
   * 将任务运行数据分发给所有注册的处理器，单个处理器异常不影响其他处理器。
   *
   * @param data 任务运行数据
   */
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
