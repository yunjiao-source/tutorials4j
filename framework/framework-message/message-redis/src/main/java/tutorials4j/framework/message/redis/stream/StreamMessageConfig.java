package tutorials4j.framework.message.redis.stream;

import java.time.Duration;
import lombok.Builder;
import tutorials4j.framework.message.core.bean.MessageConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record StreamMessageConfig(
    String queueName,
    int countPreRead,
    Duration sleepWhenExcption,
    Duration pendingTimeout,
    int maxRetryCount,
    Duration claimMinIdleTime) {
  public static final String MESSAGE_TYPE = "stream";

  public String getMainQueue() {
    return MessageConsts.getMessageQueueMain(MESSAGE_TYPE + ":" + queueName);
  }

  public String getDeadLetterQueue() {
    return MessageConsts.getMessageQueueDeadLetter(MESSAGE_TYPE + ":" + queueName);
  }
}
