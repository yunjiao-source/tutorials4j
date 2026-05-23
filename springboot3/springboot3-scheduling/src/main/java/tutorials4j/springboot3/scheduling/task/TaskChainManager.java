package tutorials4j.springboot3.scheduling.task;

import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 * 构建有依赖关系的任务链
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskChainManager {

  private final TaskScheduler taskScheduler;
  private final Map<String, ScheduledFuture<?>> taskFutures = new ConcurrentHashMap<>();

  public void createTaskChain(List<TaskDefinition> tasks) {
    for (int i = 0; i < tasks.size(); i++) {
      TaskDefinition task = tasks.get(i);
      TaskDefinition nextTask = i < tasks.size() - 1 ? tasks.get(i + 1) : null;

      scheduleTaskWithDependency(task, nextTask);
    }
  }

  private void scheduleTaskWithDependency(TaskDefinition task, TaskDefinition nextTask) {
    ScheduledFuture<?> future =
        taskScheduler.schedule(
            () -> {
              try {
                task.execute();
                if (nextTask != null) {
                  scheduleTaskWithDependency(nextTask, null);
                }
              } catch (Exception e) {
                // 处理失败，可能触发重试或通知
                handleTaskFailure(task, e);
              }
            },
            Instant.now().plus(task.getInitialDelay()));

    taskFutures.put(task.getId(), future);
  }

  private void handleTaskFailure(TaskDefinition task, Exception e) {}

  public void cancelTaskChain(String chainId) {
    // 取消相关任务
  }

  @Data
  public static class TaskDefinition {
    private String id;
    private Duration initialDelay;

    public void execute() {}
  }

  @PreDestroy
  public void stop() {
    taskFutures.forEach(
        (k, v) -> {
          log.info("Stop taks : {}", k);
          v.cancel(false);
        });
  }
}
