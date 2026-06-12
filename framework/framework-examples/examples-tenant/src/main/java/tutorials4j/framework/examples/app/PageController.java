package tutorials4j.framework.examples.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("spring-cache")
  public String springCache() {
    return "spring-cache";
  }

  @GetMapping("jpa-database")
  public String jpaDatabase() {
    return "jpa-database";
  }

  @GetMapping("jpa-table")
  public String jpaTable() {
    return "jpa-table";
  }

  @GetMapping("mybatis-table")
  public String mybatisTable() {
    return "mybatis-table";
  }

  @GetMapping("mybatis-database")
  public String mybatisDatabase() {
    return "mybatis-database";
  }
}
