package tutorials4j.framework.examples.hibernate.secondlevelcache;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

  @GetMapping("secondlevelcache")
  public String secondlevelcache() {
    return "hibernate/secondlevelcache";
  }
}
