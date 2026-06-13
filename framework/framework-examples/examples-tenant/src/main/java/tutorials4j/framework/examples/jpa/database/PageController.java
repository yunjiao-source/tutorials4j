package tutorials4j.framework.examples.jpa.database;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  @GetMapping("jpa-database")
  public String jpaDatabase() {
    return "jpa-database";
  }
}
