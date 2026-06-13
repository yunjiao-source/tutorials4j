package tutorials4j.springcloud.oauth.simple;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/** Refresh Token 并发刷新控制 */
@Service
@RequiredArgsConstructor
public class RefreshTokenGuardService {

  private final StringRedisTemplate redisTemplate;

  public boolean tryAcquire(String refreshTokenJti) {
    String key = "auth:refresh-lock:" + refreshTokenJti;
    Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(5));
    return Boolean.TRUE.equals(success);
  }

  public void release(String refreshTokenJti) {
    redisTemplate.delete("auth:refresh-lock:" + refreshTokenJti);
  }
}
