package tutorials4j.springboot3.scheduling.task;

import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Component;

/**
 * 支持多种触发策略的通用调度器
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UniversalTaskScheduler {

  private final TaskScheduler taskScheduler;
  private final Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

  public String scheduleTask(Runnable task, TriggerStrategy strategy) {
    String taskId = UUID.randomUUID().toString();

    ScheduledFuture<?> future =
        taskScheduler.schedule(
            () -> {
              try {
                task.run();
                strategy.onSuccess();
              } catch (Exception e) {
                strategy.onFailure(e);
              }
            },
            strategy.nextExecutionTime(null));

    tasks.put(taskId, future);
    return taskId;
  }

  @PreDestroy
  public void stop() {
    tasks.forEach(
        (k, v) -> {
          log.info("Stop taks : {}", k);
          v.cancel(false);
        });
  }

  public interface TriggerStrategy {
    Instant nextExecutionTime(@Nullable TriggerContext triggerContext);

    void onSuccess();

    void onFailure(Exception e);
  }

  // 示例策略
  public static class ExponentialBackoffTrigger implements TriggerStrategy {
    private Duration initialDelay = Duration.ofSeconds(5);
    private Duration maxDelay = Duration.ofMinutes(5);
    private int maxAttempts = 10;
    private int currentAttempt = 0;

    @Override
    public Instant nextExecutionTime(TriggerContext triggerContext) {
      if (currentAttempt >= maxAttempts) {
        return null; // 停止重试
      }

      long delay =
          Math.min(
              initialDelay.toMillis() * (long) Math.pow(2, currentAttempt), maxDelay.toMillis());

      return Instant.now().plusMillis(delay);
    }

    @Override
    public void onSuccess() {
      currentAttempt = 0; // 重置重试计数器
    }

    @Override
    public void onFailure(Exception e) {
      currentAttempt++;
    }
  }
}
