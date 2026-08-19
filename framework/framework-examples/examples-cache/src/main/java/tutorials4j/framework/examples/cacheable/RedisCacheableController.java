package tutorials4j.framework.examples.cacheable;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.core.TenantContextHolder;
import tutorials4j.framework.examples.Car;

/**
 * Redis 缓存示例接口，提供基于 Redis 缓存管理器的数据查询示例。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/redis/cacheable")
@RequiredArgsConstructor
public class RedisCacheableController {
  private final RedisCacheableService redisCacheableService;

  /**
   * 查询用户数据（演示 Redis 缓存命中）。
   *
   * @param id 用户 ID
   * @return 用户数据字符串
   */
  @GetMapping("users")
  public String getUser(@RequestParam("id") Long id) {
    return redisCacheableService.getUser(id);
  }

  /**
   * 查询订单数据（演示 Redis 缓存命中）。
   *
   * @param id 订单 ID
   * @return 订单数据字符串
   */
  @GetMapping("orders")
  public String getOrder(@RequestParam("id") Long id) {
    return redisCacheableService.getOrder(id);
  }

  /**
   * 查询汽车对象（演示 Redis 缓存命中）。
   *
   * @param id 汽车 ID
   * @return 汽车对象
   */
  @GetMapping("cars")
  public Car getCar(@RequestParam("id") Long id) {
    return redisCacheableService.getCar(id);
  }

  /**
   * 在指定租户上下文（DEMO）下查询用户，演示多租户缓存键隔离。
   *
   * @param id 用户 ID
   * @return 用户数据字符串
   */
  @GetMapping("tenant-users")
  public String getTenantUser(@RequestParam("id") Long id) {
    TenantContextHolder.set("DEMO");
    String user = redisCacheableService.getUser(id);
    TenantContextHolder.clear();
    return user;
  }
}
