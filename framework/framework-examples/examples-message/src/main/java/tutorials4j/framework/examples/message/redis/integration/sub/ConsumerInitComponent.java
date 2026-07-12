package tutorials4j.framework.examples.message.redis.integration.sub;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class ConsumerInitComponent implements CommandLineRunner {
  private final DemoRedisService demoRedisService;

  @Override
  public void run(String... args) throws Exception {
    // demoRedisService.init();
  }
}
