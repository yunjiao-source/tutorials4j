package tutorials4j.framework.examples.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试执行接口
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class OrderRedisController {
  private final OrderRedisService orderRedisService;

  @GetMapping("non-expire-time")
  public void nonExpireTime(@RequestParam("orderId") String orderId) {
    orderRedisService.nonExpireTime(orderId);
  }

  @GetMapping("expire-time")
  public void expireTime(@RequestParam("orderId") String orderId) {
    orderRedisService.expireTime(orderId);
  }
}
