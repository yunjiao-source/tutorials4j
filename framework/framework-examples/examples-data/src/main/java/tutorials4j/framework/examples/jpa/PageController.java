package tutorials4j.framework.examples.jpa;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * JPA 示例页面控制器。
 *
 * <p>提供 JPA 示例相关页面的访问入口。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  /**
   * 返回 JPA 列表示例页面。
   *
   * @return 视图名称
   */
  @GetMapping("list")
  public String list() {
    return "jpa/list";
  }
}
