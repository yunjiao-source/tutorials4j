package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequestMapping("demo")
public class DemoController {
  @GetMapping("get")
  public String get() {
    return "get";
  }
}
