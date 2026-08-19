package tutorials4j.framework.examples.mybatis;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MyBatis 增删改查示例的页面控制器。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  /**
   * 跳转到 MyBatis 增删改查示例页面。
   *
   * @return 视图名称
   */
  @GetMapping("curd")
  public String curd() {
    return "mybatis/curd";
  }
}
