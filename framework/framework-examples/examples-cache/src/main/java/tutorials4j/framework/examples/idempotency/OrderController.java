package tutorials4j.framework.examples.idempotency;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("order")
public class OrderController {
  private final OrderService orderService;

  @PostMapping("/{orderId}")
  public String handler(@PathVariable("orderId") String orderId) {
    return orderService.handler(orderId);
  }
}
