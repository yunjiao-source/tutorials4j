package tutorials4j.framework.examples.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Redis 锁示例测试控制器。
 *
 * <p>提供基于 Redis 分布式锁的订单操作测试接口，用于演示 Redis 锁的互斥效果。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class OrderRedisController {
  private final OrderRedisService orderRedisService;

  /**
   * 测试 Redis 锁的无过期时间模式：锁持有期间不会自动过期。
   *
   * @param orderId 订单编号
   */
  @GetMapping("non-expire-time")
  public void nonExpireTime(@RequestParam("orderId") String orderId) {
    orderRedisService.nonExpireTime(orderId);
  }

  /**
   * 测试 Redis 锁的过期时间模式：锁超过过期时间后自动释放。
   *
   * @param orderId 订单编号
   */
  @GetMapping("expire-time")
  public void expireTime(@RequestParam("orderId") String orderId) {
    orderRedisService.expireTime(orderId);
  }
}
