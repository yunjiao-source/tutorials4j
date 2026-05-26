package tutorials4j.springboot3.schedule.remindtask.task;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.common.jpa.RemindTask;
import tutorials4j.springboot3.common.jpa.RemindTaskRepository;
import tutorials4j.springboot3.common.jpa.RemindTaskService;

/**
 * 任务管理
 *
 * @author yangyunjiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleTaskManager implements SchedulingConfigurer {
  private final RemindTaskRepository remindTaskRepository;
  private final RemindTaskService remindTaskService;
  private final ApplicationContext applicationContext;

  private ScheduledTaskRegistrar scheduledTaskRegistrar;

  // 用于存储已注册过的调度任务
  private final Map<Long, ScheduledTask> triggerTaskMap = new ConcurrentHashMap<>();

  public void initTasks() {
    remindTaskRepository.findAll().forEach(this::addTask);
  }

  /** 调度任务列表 */
  public List<RemindTask> taskList() {
    Set<Long> ids = triggerTaskMap.keySet();
    return this.remindTaskRepository.findAllById(ids);
  }

  /** 启动调度任务 */
  public synchronized void addTask(RemindTask task) {
    // 启动任务前,需要先判断启动任务的状态,如果是已启动,就不能重复启动,抛出异常提示
    if (triggerTaskMap.containsKey(task.getId())) {
      throw new RuntimeException("调度任务已启动,不能重复添加已启动的任务");
    }
    // 如果待启动的调度任务处于停止状态,则定义一个触发器任务
    TriggerTask triggerTask =
        new TriggerTask(
            new CustomTriggerTask(task, remindTaskService, applicationContext),
            new CronTrigger(task.getCron()));
    // 注册并开始执行触发器调度任务
    ScheduledTask scheduledTask = this.scheduledTaskRegistrar.scheduleTriggerTask(triggerTask);
    // 注册调度任务成功后,把执行结果缓存起来,用于调度任务的动态管理
    this.triggerTaskMap.put(task.getId(), scheduledTask);

    log.info("添加任务成功：{}", task);
  }

  /** 停止调度任务 */
  public synchronized void cancel(Long id) {
    remindTaskRepository
        .findById(id)
        .map(task -> triggerTaskMap.get(task.getId()))
        .ifPresent(ScheduledTask::cancel);
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    this.scheduledTaskRegistrar = taskRegistrar;
    initTasks();
  }
}
