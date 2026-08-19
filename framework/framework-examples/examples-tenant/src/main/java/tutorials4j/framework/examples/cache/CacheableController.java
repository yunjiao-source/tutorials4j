package tutorials4j.framework.examples.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Cache 缓存示例控制器。
 *
 * <p>提供用户、订单与汽车数据的查询接口，演示不同缓存名称下的缓存命中效果。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/cacheable")
@RequiredArgsConstructor
public class CacheableController {
  private final CacheableService cacheableService;

  /**
   * 查询用户信息（结果缓存在 {@code users} 缓存中）。
   *
   * @param id 用户 ID
   * @return 用户信息字符串
   */
  @GetMapping("users")
  public String getUser(@RequestParam("id") Long id) {
    return cacheableService.getUser(id);
  }

  /**
   * 查询订单信息（结果缓存在 {@code orders} 缓存中）。
   *
   * @param id 订单 ID
   * @return 订单信息字符串
   */
  @GetMapping("orders")
  public String getOrder(@RequestParam("id") Long id) {
    return cacheableService.getOrder(id);
  }

  /**
   * 查询汽车信息（结果缓存在 {@code cars} 缓存中）。
   *
   * @param id 汽车 ID
   * @return 汽车信息字符串
   */
  @GetMapping("cars")
  public String getCar(@RequestParam("id") Long id) {
    return cacheableService.getCar(id);
  }
}
