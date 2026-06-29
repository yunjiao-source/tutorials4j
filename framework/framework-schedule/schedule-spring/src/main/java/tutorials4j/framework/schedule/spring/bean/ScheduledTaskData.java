package tutorials4j.framework.schedule.spring.bean;

import lombok.Builder;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.TriggerTask;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record ScheduledTaskData(
    ScheduledTask scheduledTask, RunnableDecorator runner, TriggerTask triggerTask) {}
