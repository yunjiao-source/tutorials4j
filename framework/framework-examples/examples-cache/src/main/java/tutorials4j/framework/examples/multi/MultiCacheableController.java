package tutorials4j.framework.examples.multi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.examples.Car;

/**
 * 多级缓存示例控制器。
 *
 * <p>提供用户、订单、汽车三类数据的查询接口，用于演示多级缓存（本地缓存 + Redis）的存取效果。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/multi-level/cacheable")
@RequiredArgsConstructor
public class MultiCacheableController {
  private final MultiCacheableService multiCacheableService;

  /**
   * 根据 ID 查询用户数据（走多级缓存）。
   *
   * @param id 用户 ID
   * @return 用户数据
   */
  @GetMapping("users")
  public String getUser(@RequestParam("id") Long id) {
    return multiCacheableService.getUser(id);
  }

  /**
   * 根据 ID 查询订单数据（走多级缓存）。
   *
   * @param id 订单 ID
   * @return 订单数据
   */
  @GetMapping("orders")
  public String getOrder(@RequestParam("id") Long id) {
    return multiCacheableService.getOrder(id);
  }

  /**
   * 根据 ID 查询汽车信息（走多级缓存）。
   *
   * @param id 汽车 ID
   * @return 汽车信息
   */
  @GetMapping("cars")
  public Car getCar(@RequestParam("id") Long id) {
    return multiCacheableService.getCar(id);
  }
}
