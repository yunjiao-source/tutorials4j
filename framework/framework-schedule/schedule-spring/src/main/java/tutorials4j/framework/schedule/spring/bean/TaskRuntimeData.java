package tutorials4j.framework.schedule.spring.bean;

import java.time.Instant;
import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record TaskRuntimeData(
    TaskStatusEnum taskStatus,
    Instant timestamp,
    String taskCode,
    String lotNo,
    int totalCount,
    int totalFailureCount,
    Instant startTime,
    Instant endTime,
    String message,
    Throwable throwable) {}
