package tutorials4j.framework.examples.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Redisson 锁示例测试控制器。
 *
 * <p>提供基于 Redisson 的阻塞锁与可重入锁测试接口，用于演示 Redisson 分布式锁的互斥效果。
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequestMapping("/redisson")
@RequiredArgsConstructor
public class OrderRedissonController {
  private final OrderRedissonService orderRedissonService;

  /**
   * 测试 Redisson 阻塞锁的无过期时间模式：锁在业务执行期间不会自动过期。
   *
   * @param orderId 订单编号
   */
  @GetMapping("block-non-expire-time")
  public void blockNonExpireTime(@RequestParam("orderId") String orderId) {
    orderRedissonService.blockNonExpireTime(orderId);
  }

  /**
   * 测试 Redisson 阻塞锁的过期时间模式：锁在指定过期时间后自动释放。
   *
   * @param orderId 订单编号
   */
  @GetMapping("block-expire-time")
  public void blockExpireTime(@RequestParam("orderId") String orderId) {
    orderRedissonService.blockExpireTime(orderId);
  }

  /**
   * 测试 Redisson 可重入锁的无过期时间模式：同一线程可重复加锁。
   *
   * @param orderId 订单编号
   */
  @GetMapping("reentrant-non-expire-time")
  public void reentrantNonExpireTime(@RequestParam("orderId") String orderId) {
    orderRedissonService.reentrantNonExpireTime(orderId);
  }

  /**
   * 测试 Redisson 可重入锁的过期时间模式：锁在指定过期时间后自动释放。
   *
   * @param orderId 订单编号
   */
  @GetMapping("reentrant-expire-time")
  public void reentrantExpireTime(@RequestParam("orderId") String orderId) {
    orderRedissonService.reentrantExpireTime(orderId);
  }
}
