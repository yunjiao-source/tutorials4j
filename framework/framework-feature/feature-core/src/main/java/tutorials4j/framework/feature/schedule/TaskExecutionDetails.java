package tutorials4j.framework.feature.schedule;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record TaskExecutionDetails(
    String taskCode,
    String classSimpleName,
    String cron,
    boolean enabled,
    String description,
    Map<String, String> metadata,
    Duration initialDelay,
    Integer maxFailureCount,
    Integer maxExecutionCount,
    Instant dueDate,
    String lotNo,
    int totalCount,
    int totalFailureCount,
    Instant startTime,
    Instant endTime,
    String message,
    Throwable throwable) {}
