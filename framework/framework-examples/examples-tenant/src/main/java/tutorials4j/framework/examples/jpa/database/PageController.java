package tutorials4j.framework.examples.jpa.database;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面控制器，负责展示 JPA 数据库示例页面。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  /**
   * 返回 JPA 数据库示例页面视图。
   *
   * @return 视图名称 "jpa-database"
   */
  @GetMapping("jpa-database")
  public String jpaDatabase() {
    return "jpa-database";
  }
}
