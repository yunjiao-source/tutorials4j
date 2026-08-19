package tutorials4j.framework.examples.mybatis.table;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 用户示例页面视图控制器，用于展示 MyBatis-Plus 分表（表级租户隔离）示例页面。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  /**
   * 跳转到 MyBatis 分表示例页面。
   *
   * @return 分表示例页面视图名称
   */
  @GetMapping("mybatis-table")
  public String mybatisTable() {
    return "mybatis-table";
  }
}
