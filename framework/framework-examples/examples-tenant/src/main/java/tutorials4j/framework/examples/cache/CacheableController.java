package tutorials4j.framework.examples.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/cacheable")
@RequiredArgsConstructor
public class CacheableController {
  private final CacheableService cacheableService;

  @GetMapping("users")
  public String getUser(@RequestParam("id") Long id) {
    return cacheableService.getUser(id);
  }

  @GetMapping("orders")
  public String getOrder(@RequestParam("id") Long id) {
    return cacheableService.getOrder(id);
  }

  @GetMapping("cars")
  public String getCar(@RequestParam("id") Long id) {
    return cacheableService.getCar(id);
  }
}
