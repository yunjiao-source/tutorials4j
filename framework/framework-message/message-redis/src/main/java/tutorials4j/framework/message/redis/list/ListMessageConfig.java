package tutorials4j.framework.message.redis.list;

import java.time.Duration;
import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record ListMessageConfig(String queueName, Duration blockTimeout) {}
