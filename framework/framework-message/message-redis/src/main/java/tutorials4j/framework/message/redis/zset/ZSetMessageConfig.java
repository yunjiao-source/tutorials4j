package tutorials4j.framework.message.redis.zset;

import java.time.Duration;
import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record ZSetMessageConfig(
    String delayQueueName, String processQueueName, Duration blockTimeout) {}
