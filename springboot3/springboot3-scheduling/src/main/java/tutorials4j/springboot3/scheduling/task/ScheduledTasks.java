package tutorials4j.springboot3.scheduling.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 *
 * @author Yun Jiao
 */
@Component
public class ScheduledTasks {

  @Scheduled(fixedRate = 5000)
  public void reportCurrentTime() {
    System.out.println("Current Time: " + System.currentTimeMillis());
  }

  @Scheduled(fixedRate = 10000)
  public void performTask() {
    System.out.println("Fixed rate task executed at " + System.currentTimeMillis());
  }

  @Scheduled(fixedDelay = 15000)
  public void performDelayedTask() {
    System.out.println("Fixed delay task executed at " + System.currentTimeMillis());
  }

  @Scheduled(cron = "0 0/2 * * * ?")
  public void performCronTask() {
    System.out.println("Cron task executed at " + System.currentTimeMillis());
  }
}
