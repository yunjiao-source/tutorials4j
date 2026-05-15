package tutorials4j.springboot3;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例接口
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("demo")
@RequiredArgsConstructor
public class DemoController {
  private final DemoService demoService;

  @GetMapping
  public void demo() {
    int count = ThreadLocalRandom.current().nextInt(9);
    IntStream.range(0, count).forEach(e -> demoService.qps1());

    count = ThreadLocalRandom.current().nextInt(9);
    IntStream.range(0, count).forEach(e -> demoService.qps2());
  }
}
