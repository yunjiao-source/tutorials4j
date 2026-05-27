package tutorials4j.springboot3.schedule.dynamiccron;

import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

/**
 * 配置
 *
 * @author yangyunjiao
 */
@Data
@Slf4j
@Component
@PropertySource("classpath:/dynamiccron.ini")
public class PrintTimeSchedulingConfigurer implements SchedulingConfigurer {

  @Value("${printTime.cron}")
  private String cron;

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    log.info("配置任务：{}", cron);

    // 动态使用cron表达式设置循环间隔
    taskRegistrar.addTriggerTask(
        new Runnable() {
          @Override
          public void run() {
            log.info("Current time： {}", LocalDateTime.now());
          }
        },
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
