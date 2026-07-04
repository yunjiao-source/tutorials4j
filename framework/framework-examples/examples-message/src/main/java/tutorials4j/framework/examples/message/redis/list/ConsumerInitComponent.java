package tutorials4j.framework.examples.message.redis.list;

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
  private final SmsService smsService;

  @Override
  public void run(String... args) throws Exception {
    smsService.init();
  }
}
