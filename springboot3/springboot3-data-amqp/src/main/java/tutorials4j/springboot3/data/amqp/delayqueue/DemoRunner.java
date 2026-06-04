package tutorials4j.springboot3.data.amqp.delayqueue;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单生成
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DemoRunner {
  private final OrderService orderService;

  @Scheduled(fixedDelay = 10000)
  public void demoData() {
    IntStream.range(0, 10)
        .forEach(
            i -> {
              try {
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(999));
              } catch (InterruptedException e) {
                throw new RuntimeException(e);
              }
              orderService.createOrder(new OrderDTO("data-" + i));
            });
  }
}
