package tutorials4j.springcloud.oauth.simple;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Refresh Token 并发刷新控制服务。
 *
 * <p>基于 Redis 的 SETNX 锁防止同一 Refresh Token 被并发刷新，锁的 TTL 为 5 秒。
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenGuardService {

  private final StringRedisTemplate redisTemplate;

  /**
   * 尝试获取指定 Refresh Token 的刷新锁。
   *
   * @param refreshTokenJti Refresh Token 的 JTI
   * @return 获取成功返回 true，已被其他请求占用返回 false
   */
  public boolean tryAcquire(String refreshTokenJti) {
    String key = "auth:refresh-lock:" + refreshTokenJti;
    Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(5));
    return Boolean.TRUE.equals(success);
  }

  /**
   * 释放指定 Refresh Token 的刷新锁。
   *
   * @param refreshTokenJti Refresh Token 的 JTI
   */
  public void release(String refreshTokenJti) {
    redisTemplate.delete("auth:refresh-lock:" + refreshTokenJti);
  }
}
