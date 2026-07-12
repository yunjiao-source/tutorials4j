package tutorials4j.framework.message.redis.bean;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.Duration;
import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.common.spring.util.SnowflakeUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RedisMessage {
  @EqualsAndHashCode.Include private String id;
  private Instant timestamp;
  private String name;
  private int retryCount;
  private String data;
  private Duration delayTime;
  private String failureReasons;

  public static RedisMessage defaultValue() {
    RedisMessage message = new RedisMessage();
    message.setId(SnowflakeUtils.nextIdStr()).setTimestamp(Instant.now()).setRetryCount(1);
    return message;
  }

  public RedisMessage addReason(String reason) {
    if (StringUtils.isBlank(failureReasons)) {
      failureReasons = reason;
    } else {
      failureReasons = failureReasons + ";" + reason;
    }
    return this;
  }

  public RedisMessage increaseRetryCount() {
    retryCount += 1;
    return this;
  }
}
