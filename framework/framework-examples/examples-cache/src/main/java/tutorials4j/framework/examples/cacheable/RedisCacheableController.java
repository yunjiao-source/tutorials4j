package tutorials4j.framework.examples.cacheable;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.core.TenantContextHolder;
import tutorials4j.framework.examples.Car;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/redis/cacheable")
@RequiredArgsConstructor
public class RedisCacheableController {
  private final RedisCacheableService redisCacheableService;

  @GetMapping("users")
  public String getUser(@RequestParam("id") Long id) {
    return redisCacheableService.getUser(id);
  }

  @GetMapping("orders")
  public String getOrder(@RequestParam("id") Long id) {
    return redisCacheableService.getOrder(id);
  }

  @GetMapping("cars")
  public Car getCar(@RequestParam("id") Long id) {
    return redisCacheableService.getCar(id);
  }

  @GetMapping("tenant-users")
  public String getTenantUser(@RequestParam("id") Long id) {
    TenantContextHolder.set("DEMO");
    return redisCacheableService.getUser(id);
  }
}
