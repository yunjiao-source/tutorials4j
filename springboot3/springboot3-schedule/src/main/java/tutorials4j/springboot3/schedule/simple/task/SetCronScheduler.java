package tutorials4j.springboot3.schedule.simple.task;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

/**
 * 设置
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SetCronScheduler {
  @Setter private String cron;
  private final TaskScheduler taskScheduler;

  public ScheduledFuture<?> scheduleTask(Runnable task) {

    return taskScheduler.schedule(
        task,
        new Trigger() {
          @Override
          public Instant nextExecution(TriggerContext triggerContext) {
            // 使用CronTrigger触发器，可动态修改cron表达式来操作循环规则
            CronTrigger cronTrigger = new CronTrigger(cron);
            return cronTrigger.nextExecution(triggerContext);
          }
        });
  }
}
