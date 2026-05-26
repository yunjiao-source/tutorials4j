package tutorials4j.springboot3.integration.restsignature;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Nonce 服务（防重放）
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class NonceService {
  private final RedisTemplate<String, String> redisTemplate;

  private static final String NONCE_PREFIX = "signature:nonce:";

  public boolean exists(String nonce) {
    return redisTemplate.hasKey(NONCE_PREFIX + nonce);
  }

  public void save(String nonce, long ttl) {
    redisTemplate.opsForValue().set(NONCE_PREFIX + nonce, "1", ttl, TimeUnit.SECONDS);
  }
}
