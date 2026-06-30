package tutorials4j.framework.feature.signin.service;

import java.time.Duration;
import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record SignInConfig(String source, String keyPrefix, int maxBits, Duration expireTime) {}
