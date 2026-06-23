package tutorials4j.springboot3.demo2;

import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DemoRunner implements CommandLineRunner {
  private final OrderLockService orderLockService;

  @Override
  public void run(String... args) throws Exception {
    IntStream.range(0, 10)
        .forEach(
            i -> {
              log.info("=== 第{}次 ===", i);
              orderLockService.execute();
            });
  }
}
