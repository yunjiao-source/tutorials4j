package tutorials4j.framework.examples.app;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SpringUtil 工具类测试控制器。
 *
 * <p>提供接口验证 {@code SpringUtil} 获取 Spring Bean 的能力。
 *
 * @author Yun Jiao
 */
@RequestMapping("spring-util")
@RestController
public class SpringUtilController {

  /**
   * 通过 SpringUtil 获取当前控制器 Bean，验证其可用性。
   *
   * @return 获取到 Bean 时返回 {@code true}
   */
  @GetMapping("/getBean")
  public boolean getBean() {
    Object bean = SpringUtil.getBean(SpringUtilController.class);
    return bean != null;
  }
}
