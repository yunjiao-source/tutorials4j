package tutorials4j.framework.examples.idempotency;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;
import tutorials4j.framework.cache.redis.idempotency.IdempotentGuard;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
public class OrderService {

  @IdempotentGuard(prefix = "idempotent:order:", key = "#orderId", expireMills = 10000)
  public String handler(String orderId) {
    if (ThreadLocalRandom.current().nextInt(10) < 5) {
      throw new RuntimeException("异常");
    }
    return "订单处理成功";
  }
}
