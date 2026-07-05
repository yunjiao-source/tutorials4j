package tutorials4j.framework.examples.message.redis.list;

import java.time.Instant;
import tutorials4j.framework.message.core.bean.MessageData;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public record SmsData(Long id, Instant Date) implements MessageData {}
