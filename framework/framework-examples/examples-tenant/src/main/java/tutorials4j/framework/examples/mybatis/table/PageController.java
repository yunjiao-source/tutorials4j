package tutorials4j.framework.examples.mybatis.table;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  @GetMapping("mybatis-table")
  public String mybatisTable() {
    return "mybatis-table";
  }
}
