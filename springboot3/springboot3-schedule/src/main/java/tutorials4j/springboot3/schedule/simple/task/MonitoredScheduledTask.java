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
 * 任务状态监控
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MonitoredScheduledTask {

  private final TaskScheduler taskScheduler;

  private ScheduledFuture<?> scheduledFuture;

  @PostConstruct
  public void scheduleTask() {
    scheduledFuture = taskScheduler.scheduleAtFixedRate(this::performTask, Duration.ofSeconds(10));
  }

  public void cancelTask() {
    if (scheduledFuture != null) {
      scheduledFuture.cancel(false);
    }
  }

  private void performTask() {
    log.info("Monitored scheduled task executed at {}", System.currentTimeMillis());
  }

  @PreDestroy
  public void stop() {
    if (scheduledFuture != null) {
      scheduledFuture.cancel(false);
    }
  }
}
