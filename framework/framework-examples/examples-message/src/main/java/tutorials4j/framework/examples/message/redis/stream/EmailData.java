package tutorials4j.framework.examples.message.redis.stream;

import java.time.Instant;
import tutorials4j.framework.message.core.bean.MessageData;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public record EmailData(Long id, Instant Date) implements MessageData {}
