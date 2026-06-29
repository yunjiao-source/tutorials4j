package tutorials4j.framework.schedule.spring.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import tutorials4j.framework.common.core.util.MapUtils;
import tutorials4j.framework.schedule.core.bean.Task;
import tutorials4j.framework.schedule.spring.bean.TaskExecutionDetails;
import tutorials4j.framework.schedule.spring.bean.TaskRuntimeData;
import tutorials4j.framework.schedule.spring.repository.TaskRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class ScheduleService {
  private final ScheduleTaskManager scheduleTaskManager;
  private final TaskRepository<?> taskRepository;

  public TaskExecutionDetails cancelTask(String taskCode) {
    Optional<? extends Task> taskOpt = taskRepository.findByTaskCode(taskCode);
    if (taskOpt.isPresent()) {
      Task task = taskOpt.get();
      scheduleTaskManager.cancelTask(task.getTaskCode());
      return buildDetails(task);
    }

    return null;
  }

  public TaskExecutionDetails startTask(String taskCode) {
    Optional<? extends Task> taskOpt = taskRepository.findByTaskCode(taskCode);
    if (taskOpt.isPresent()) {
      Task task = taskOpt.get();
      scheduleTaskManager.createTask(task);
      return buildDetails(task);
    }

    return null;
  }

  public TaskExecutionDetails getTaskDetails(String taskCode) {
    return taskRepository.findByTaskCode(taskCode).map(this::buildDetails).orElse(null);
  }

  public List<TaskExecutionDetails> getAllTaskDetails() {
    List<? extends Task> allTask = taskRepository.findAll();
    if (allTask.isEmpty()) {
      return Collections.emptyList();
    }

    List<TaskExecutionDetails> detailsList = new ArrayList<>();
    for (Task task : allTask) {
      TaskExecutionDetails details = buildDetails(task);
      detailsList.add(details);
    }

    return detailsList;
  }

  private TaskExecutionDetails buildDetails(Task task) {
    TaskExecutionDetails.TaskExecutionDetailsBuilder builder =
        TaskExecutionDetails.builder()
            .taskCode(task.getTaskCode())
            .classSimpleName(task.getClassSimpleName())
            .cron(task.getCron())
            .enabled(task.isEnabled())
            .description(task.getDescription())
            .metadata(MapUtils.unmodifiableMap(task.getMetadata()))
            .initialDelay(task.getInitialDelay())
            .maxExecutionCount(task.getMaxExecutionCount())
            .maxFailureCount(task.getMaxFailureCount())
            .dueDate(task.getDueDate());

    // 获取运行的任务信息
    Task runningTask = scheduleTaskManager.getTask(task.getTaskCode());
    if (runningTask != null) {
      builder
          .taskCode(runningTask.getTaskCode())
          .classSimpleName(runningTask.getClassSimpleName())
          .cron(runningTask.getCron())
          .enabled(runningTask.isEnabled())
          .description(runningTask.getDescription())
          .metadata(MapUtils.unmodifiableMap(runningTask.getMetadata()))
          .initialDelay(runningTask.getInitialDelay())
          .maxExecutionCount(runningTask.getMaxExecutionCount())
          .maxFailureCount(runningTask.getMaxFailureCount())
          .dueDate(runningTask.getDueDate());
    }

    // 获取运行信息
    TaskRuntimeData lastTaskRuntimeData =
        scheduleTaskManager.getLastTaskRuntimeData(task.getTaskCode());
    if (lastTaskRuntimeData != null) {
      builder
          .taskStatus(lastTaskRuntimeData.taskStatus())
          .lotNo(lastTaskRuntimeData.lotNo())
          .totalCount(lastTaskRuntimeData.totalCount())
          .totalFailureCount(lastTaskRuntimeData.totalFailureCount())
          .startTime(lastTaskRuntimeData.startTime())
          .endTime(lastTaskRuntimeData.endTime())
          .message(lastTaskRuntimeData.message())
          .throwable(lastTaskRuntimeData.throwable());
    }
    return builder.build();
  }
}
