package tutorials4j.springboot3.schedule.simple.task;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 * 高级定时任务管理: 动态修改任务执行时间
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicScheduledTask {
  private final TaskScheduler taskScheduler;

  private ScheduledFuture<?> scheduledFuture;

  @PostConstruct
  public void scheduleTask() {
    scheduledFuture = taskScheduler.scheduleAtFixedRate(this::performTask, Duration.ofSeconds(5));
  }

  public void changeTaskInterval(Duration interval) {
    if (scheduledFuture != null) {
      scheduledFuture.cancel(false);
    }
    scheduledFuture = taskScheduler.scheduleAtFixedRate(this::performTask, interval);
  }

  private void performTask() {
    log.info("Dynamic scheduled task executed at {}", System.currentTimeMillis());
  }

  @PreDestroy
  public void stop() {
    if (scheduledFuture != null) {
      scheduledFuture.cancel(false);
    }
  }
}
