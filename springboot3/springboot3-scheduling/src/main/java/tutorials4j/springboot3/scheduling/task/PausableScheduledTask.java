package tutorials4j.springboot3.scheduling.task;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 * 可暂停和恢复的任务
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PausableScheduledTask {

  private final TaskScheduler taskScheduler;
  private ScheduledFuture<?> scheduledFuture;
  private final AtomicBoolean paused = new AtomicBoolean(false);
  private final ReentrantLock lock = new ReentrantLock();

  @PostConstruct
  public void init() {
    scheduledFuture = taskScheduler.scheduleAtFixedRate(this::pausableTask, Duration.ofSeconds(10));
  }

  private void pausableTask() {
    if (paused.get()) {
      return;
    }

    if (lock.tryLock()) {
      try {
        // 执行需要加锁的业务逻辑
        performCriticalTask();
      } finally {
        lock.unlock();
      }
    } else {
      log.info("Task is already running, skipping this execution");
    }
  }

  private void performCriticalTask() {
    log.info("Pausable Scheduled Task");
  }

  public void pause() {
    paused.set(true);
  }

  public void resume() {
    paused.set(false);
  }

  @PreDestroy
  public void stop() {
    if (scheduledFuture != null) {
      scheduledFuture.cancel(false);
    }
  }

  public void restart() {
    stop();
    init();
  }
}
