package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例 REST 控制器，提供简单的接口调用演示。
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequestMapping("demo")
public class DemoController {
  /**
   * 返回固定字符串 "get" 的示例接口。
   *
   * @return 固定字符串 "get"
   */
  @GetMapping("get")
  public String get() {
    return "get";
  }
}
