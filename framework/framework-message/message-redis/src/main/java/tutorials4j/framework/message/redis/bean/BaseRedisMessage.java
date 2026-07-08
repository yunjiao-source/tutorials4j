package tutorials4j.framework.message.redis.bean;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.common.spring.util.SnowflakeUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BaseRedisMessage {
  @EqualsAndHashCode.Include private String id;
  private String parentId;
  private Instant timestamp;
  private String queueName;
  private int retryCount;
  private String data;
  private String failureReason;

  public void defaultValue() {
    id = SnowflakeUtils.nextIdStr();
    timestamp = Instant.now();
    retryCount = 0;
  }

  public BaseRedisMessage clone() {
    String tmpParentId = StringUtils.isBlank(parentId) ? id : parentId;
    BaseRedisMessage message = new BaseRedisMessage();
    message.setId(SnowflakeUtils.nextIdStr());
    message.setParentId(tmpParentId);
    message.setTimestamp(this.getTimestamp());
    message.setRetryCount(this.retryCount);
    return message;
  }

  public void increaseRetryCount() {
    retryCount += 1;
  }
}
