package tutorials4j.springboot3.data.redis.sign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Configuration
public class Config {
  @Bean
  public DefaultRedisScript<Long> signInScript() {
    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
    redisScript.setLocation(new ClassPathResource("lua/sign_in.lua"));
    redisScript.setResultType(Long.class);
    return redisScript;
  }
}
