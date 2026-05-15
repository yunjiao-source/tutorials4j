package tutorials4j.springboot3;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
public class DemoController {
  private final DemoService demoService;

  @GetMapping("users")
  public String getUser(@RequestParam("id") Long id) {
    return demoService.getUser(id);
  }

  @GetMapping("orders")
  public String getOrder(@RequestParam("id") Long id) {
    return demoService.getOrder(id);
  }

  @GetMapping("cars")
  public String getCar(@RequestParam("id") Long id) {
    return demoService.getCars(id);
  }
}
