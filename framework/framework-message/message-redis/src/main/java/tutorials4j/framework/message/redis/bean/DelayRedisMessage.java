package tutorials4j.framework.message.redis.bean;

import java.time.Duration;
import java.util.Objects;
import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record DelayRedisMessage(BaseRedisMessage baseMessage, Duration delayTime) {
  public DelayRedisMessage cloneAndIncreaseRetryCount() {
    BaseRedisMessage baseRedisMessage = this.baseMessage.cloneAndIncreaseRetryCount();
    return DelayRedisMessage.builder().baseMessage(baseRedisMessage).delayTime(delayTime).build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DelayRedisMessage that = (DelayRedisMessage) o;
    return Objects.equals(this.baseMessage, that.baseMessage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(baseMessage);
  }
}
