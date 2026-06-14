package tutorials4j.framework.schedule.core.bean;

import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record ChangeStatusEvent(
    long timestamp,
    String taskName,
    TaskStatusEnum taskStatus,
    TaskCondition lastTaskCondition,
    String message,
    Throwable throwable) {}
