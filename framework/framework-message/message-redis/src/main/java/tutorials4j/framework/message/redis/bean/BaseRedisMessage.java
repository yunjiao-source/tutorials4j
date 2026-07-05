package tutorials4j.framework.message.redis.bean;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.common.spring.util.SnowflakeUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@Builder
public record BaseRedisMessage(
    String id,
    String parentId,
    Instant timestamp,
    String queueName,
    int retryCount,
    Map<String, String> data) {

  public BaseRedisMessage cloneAndIncreaseRetryCount() {
    String parentId = StringUtils.isBlank(this.parentId) ? this.id : this.parentId;
    return BaseRedisMessage.builder()
        .id(SnowflakeUtils.nextIdStr())
        .parentId(parentId)
        .timestamp(Instant.now())
        .queueName(this.queueName)
        .retryCount(this.retryCount + 1)
        .data(this.data)
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseRedisMessage that = (BaseRedisMessage) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
