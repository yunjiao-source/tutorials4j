package tutorials4j.framework.message.redis.zset;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.framework.message.redis.bean.BaseRedisMessage;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class DelayRedisMessage extends BaseRedisMessage {
  private Duration delayTime;
}
