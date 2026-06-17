package tutorials4j.framework.schedule.core.bean;

import java.time.Instant;
import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record ChangeStatusEvent(
    Instant timestamp,
    String taskCode,
    TaskStatusEnum taskStatus,
    TaskRuntimeData taskRuntimeData) {}
