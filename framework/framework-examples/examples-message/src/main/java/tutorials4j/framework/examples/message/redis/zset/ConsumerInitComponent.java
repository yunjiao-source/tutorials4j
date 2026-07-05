package tutorials4j.framework.examples.message.redis.zset;

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
  private final TaskService taskService;

  @Override
  public void run(String... args) throws Exception {
    taskService.init();
  }
}
