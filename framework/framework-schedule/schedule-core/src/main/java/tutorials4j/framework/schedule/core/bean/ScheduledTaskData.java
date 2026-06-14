package tutorials4j.framework.schedule.core.bean;

import lombok.Builder;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.TriggerTask;
import tutorials4j.framework.schedule.core.component.RunnableDecorator;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record ScheduledTaskData(
    ScheduledTask scheduledTask, RunnableDecorator runner, TriggerTask triggerTask) {}
