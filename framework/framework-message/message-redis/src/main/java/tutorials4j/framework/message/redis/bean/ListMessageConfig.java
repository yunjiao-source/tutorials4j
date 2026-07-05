package tutorials4j.framework.message.redis.bean;

import java.time.Duration;
import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record ListMessageConfig(
    String queueName,
    Duration blockTimeout,
    Duration sleepWhenExcption,
    Duration delayTimeout,
    int maxRetryCount) {}
