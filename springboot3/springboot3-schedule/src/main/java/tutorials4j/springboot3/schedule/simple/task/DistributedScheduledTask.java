package tutorials4j.springboot3.schedule.simple.task;

import jakarta.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 * 分布式定时任务协调
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedScheduledTask {

  private final RedisLockService lockService;
  private final TaskScheduler taskScheduler;

  @PostConstruct
  public void init() {
    taskScheduler.scheduleAtFixedRate(this::distributedTask, Duration.ofSeconds(30));
  }

  private void distributedTask() {
    String lockKey = "distributed-task-lock";
    if (lockService.tryLock(lockKey, 60)) {
      try {
        log.info("Executing distributed task on instance: {}", getInstanceId());
        // 业务逻辑
      } finally {
        lockService.unlock(lockKey);
      }
    }
  }

  private String getInstanceId() {
    return ManagementFactory.getRuntimeMXBean().getName();
  }

  /**
   * 参考 redis-distributed-lock 项目中 DistributedLock 接口实现
   *
   * @author yangyunjiao
   */
  @Component
  public static class RedisLockService {
    public boolean tryLock(String lockKey, int i) {
      return true;
    }

    public void unlock(String lockKey) {}
  }
}
