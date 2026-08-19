package tutorials4j.framework.examples.hibernate.secondlevelcache;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面控制器，负责跳转二级缓存演示页面。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  /**
   * 跳转到 Hibernate 二级缓存演示页面。
   *
   * @return 视图名称
   */
  @GetMapping("secondlevelcache")
  public String secondlevelcache() {
    return "hibernate/secondlevelcache";
  }
}
