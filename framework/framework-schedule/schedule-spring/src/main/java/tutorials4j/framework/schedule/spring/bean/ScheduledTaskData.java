package tutorials4j.framework.schedule.spring.bean;

import lombok.Builder;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.TriggerTask;

/**
 * 已注册的定时任务数据。
 *
 * <p>封装 Spring 调度器注册后的任务实例、对应的运行装饰器与触发器任务定义。
 *
 * @param scheduledTask Spring 调度器注册后的任务实例
 * @param runner 任务运行装饰器
 * @param triggerTask 触发器任务定义
 * @author Yun Jiao
 */
@Builder
public record ScheduledTaskData(
    ScheduledTask scheduledTask, RunnableDecorator runner, TriggerTask triggerTask) {}
