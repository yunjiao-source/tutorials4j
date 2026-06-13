package tutorials4j.framework.examples.mybatis.database;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("mybatis-database")
  public String mybatisDatabase() {
    return "mybatis-database";
  }
}
