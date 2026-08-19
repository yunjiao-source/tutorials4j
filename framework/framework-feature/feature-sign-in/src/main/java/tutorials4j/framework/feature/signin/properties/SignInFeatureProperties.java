package tutorials4j.framework.feature.signin.properties;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * 签到功能配置属性。
 *
 * <p>以 {@code tutorials4j.feature.sign-in} 为前缀绑定外部配置， 用于配置签到记录的 Redis 键前缀、有效期以及位图位数等。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_FEATURE_SIGN_IN)
public class SignInFeatureProperties {
  /** 签到记录 Redis 键前缀 */
  private String redisKeyPrefix = "sign-in:";

  /** 签到记录有效期 */
  private Duration expireTime = Duration.ofDays(365);

  /** 单用户最大签到位数（位图长度） */
  private int maxBits = 100_000;
}
