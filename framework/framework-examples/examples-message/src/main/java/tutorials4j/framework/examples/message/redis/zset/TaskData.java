package tutorials4j.framework.examples.message.redis.zset;

import java.time.Instant;
import tutorials4j.framework.message.core.bean.MessageData;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public record TaskData(Long id, Instant Date) implements MessageData {}
