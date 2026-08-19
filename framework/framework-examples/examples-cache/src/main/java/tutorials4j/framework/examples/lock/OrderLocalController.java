package tutorials4j.framework.examples.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 本地锁示例测试控制器。
 *
 * <p>提供基于本地锁（进程内锁）的订单操作测试接口，用于演示本地锁的互斥效果。
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequestMapping("/local")
@RequiredArgsConstructor
public class OrderLocalController {
  private final OrderLocalService orderLocalService;

  /**
   * 测试本地锁的不等待模式：不等待直接尝试加锁并执行业务。
   *
   * @param orderId 订单编号
   */
  @GetMapping("non-wait-time")
  public void nonWaitTime(@RequestParam("orderId") String orderId) {
    log.info("> {}", Thread.currentThread().getName());
    orderLocalService.nonWaitTime("1");
  }

  /**
   * 测试本地锁的等待模式：等待获取锁后再执行业务。
   *
   * @param orderId 订单编号
   */
  @GetMapping("wait-time")
  public void waitTime(@RequestParam("orderId") String orderId) {
    log.info("> {}", Thread.currentThread().getName());
    orderLocalService.waitTime(orderId);
  }
}
