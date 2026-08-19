package tutorials4j.springcloud.oauth.simple;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 令牌吊销与黑名单服务：将已吊销 Access Token 的 jti 写入 Redis 黑名单。
 *
 * <p>黑名单条目的 TTL 与 Token 剩余生命周期对齐；JWT 无状态但与黑名单结合即可实现治理能力。
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

  private final StringRedisTemplate redisTemplate;

  /**
   * 将指定 jti 的令牌加入黑名单。
   *
   * @param jti 令牌 ID
   * @param ttl 黑名单有效期
   */
  public void blacklist(String jti, Duration ttl) {
    redisTemplate.opsForValue().set(buildKey(jti), "1", ttl);
  }

  /**
   * 判断指定 jti 的令牌是否已被列入黑名单。
   *
   * @param jti 令牌 ID
   * @return 在黑名单中返回 true
   */
  public boolean isBlacklisted(String jti) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(buildKey(jti)));
  }

  /** 构建黑名单对应的 Redis 键。 */
  private String buildKey(String jti) {
    return "auth:blacklist:" + jti;
  }
}
