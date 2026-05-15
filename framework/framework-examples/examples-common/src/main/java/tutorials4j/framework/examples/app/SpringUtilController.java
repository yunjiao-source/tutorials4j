package tutorials4j.framework.examples.app;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SpringUtil 测试
 *
 * @author Yun Jiao
 */
@RequestMapping("spring-util")
@RestController
public class SpringUtilController {

  @GetMapping("/getBean")
  public boolean getBean() {
    Object bean = SpringUtil.getBean(SpringUtilController.class);
    return bean != null;
  }
}
