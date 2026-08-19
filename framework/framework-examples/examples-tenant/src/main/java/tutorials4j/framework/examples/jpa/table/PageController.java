package tutorials4j.framework.examples.jpa.table;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面控制器。
 *
 * <p>提供 JPA 多表关联示例（jpa-table）的页面入口。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  /**
   * 跳转至 JPA 多表关联示例页面。
   *
   * @return 视图名称 {@code jpa-table}
   */
  @GetMapping("jpa-table")
  public String jpaTable() {
    return "jpa-table";
  }
}
