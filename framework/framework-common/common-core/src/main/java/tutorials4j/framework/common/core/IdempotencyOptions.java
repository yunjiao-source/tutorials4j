package tutorials4j.framework.common.core;

import java.time.Duration;

/**
 * 幂等选项
 *
 * @author Yun Jiao
 */
public record IdempotencyOptions(boolean enabled, String prefix, Duration expireTime) {}
