package tutorials4j.framework.feature.signin.service;

import java.time.Duration;
import lombok.Builder;

/**
 * 签到功能配置
 *
 * <p>描述一次签到所需的配置信息：来源标识、Redis 键前缀、日活/月活位图最大位数与数据过期时间。
 *
 * @param source 签到来源标识
 * @param keyPrefix Redis 键前缀
 * @param maxBits 日活/月活位图的最大位数
 * @param expireTime 签到数据的过期时间
 * @author Yun Jiao
 */
@Builder
public record SignInConfig(String source, String keyPrefix, int maxBits, Duration expireTime) {}
