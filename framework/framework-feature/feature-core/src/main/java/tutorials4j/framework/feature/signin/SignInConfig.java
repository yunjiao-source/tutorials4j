package tutorials4j.framework.feature.signin;

import java.time.Duration;
import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record SignInConfig(String source, String keyPrefix, Duration expireTime) {}
