package tutorials4j.springcloud.oauth.simple;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 令牌吊销与黑名单 JWT 的最大优势是无状态，但无状态不等于不可治理。
 *
 * <p>一个常见可落地的方案是：
 *
 * <p>• Access Token 短期有效，例如 5~15 分钟 • Refresh Token 存储于服务端授权表 • 用户退出、密码修改、管理员封禁时，将 Access Token 的
 * jti 放入 Redis 黑名单 • 黑名单 TTL 与 Token 剩余生命周期对齐
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

  private final StringRedisTemplate redisTemplate;

  public void blacklist(String jti, Duration ttl) {
    redisTemplate.opsForValue().set(buildKey(jti), "1", ttl);
  }

  public boolean isBlacklisted(String jti) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(buildKey(jti)));
  }

  private String buildKey(String jti) {
    return "auth:blacklist:" + jti;
  }
}
