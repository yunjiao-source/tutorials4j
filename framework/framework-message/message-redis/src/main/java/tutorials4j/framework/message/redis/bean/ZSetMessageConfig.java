package tutorials4j.framework.message.redis.bean;

import java.time.Duration;
import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record ZSetMessageConfig(
    String queueName,
    Duration delayTimeout,
    Duration blockTimeout,
    Duration sleepWhenExcption,
    int maxRetryCount) {}
