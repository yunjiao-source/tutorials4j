package tutorials4j.framework.examples.jpa.table;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  @GetMapping("jpa-table")
  public String jpaTable() {
    return "jpa-table";
  }
}
