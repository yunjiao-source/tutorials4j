package tutorials4j.framework.examples.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试执行接口
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequestMapping("/redisson")
@RequiredArgsConstructor
public class OrderRedissonController {
  private final OrderRedissonService orderRedissonService;

  @GetMapping("block-non-expire-time")
  public void blockNonExpireTime(@RequestParam("orderId") String orderId) {
    orderRedissonService.blockNonExpireTime(orderId);
  }

  @GetMapping("block-expire-time")
  public void blockExpireTime(@RequestParam("orderId") String orderId) {
    orderRedissonService.blockExpireTime(orderId);
  }

  @GetMapping("reentrant-non-expire-time")
  public void reentrantNonExpireTime(@RequestParam("orderId") String orderId) {
    orderRedissonService.reentrantNonExpireTime(orderId);
  }

  @GetMapping("reentrant-expire-time")
  public void reentrantExpireTime(@RequestParam("orderId") String orderId) {
    orderRedissonService.reentrantExpireTime(orderId);
  }
}
