package tutorials4j.framework.schedule.core.bean;

import java.time.Instant;
import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record TaskCondition(
    Instant timestamp,
    int totalCount,
    int totalFailureCount,
    long startTime,
    long endTime,
    String error) {}
