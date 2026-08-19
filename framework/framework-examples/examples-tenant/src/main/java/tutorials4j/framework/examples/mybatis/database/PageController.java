package tutorials4j.framework.examples.mybatis.database;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 用户示例页面视图控制器，用于展示 MyBatis-Plus 多数据源示例页面。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  /**
   * 跳转到 MyBatis 多数据源示例页面。
   *
   * @return 多数据源示例页面视图名称
   */
  @GetMapping("mybatis-database")
  public String mybatisDatabase() {
    return "mybatis-database";
  }
}
