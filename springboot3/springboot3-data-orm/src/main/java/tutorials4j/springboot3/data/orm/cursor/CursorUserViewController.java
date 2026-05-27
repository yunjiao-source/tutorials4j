package tutorials4j.springboot3.data.orm.cursor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
@RequestMapping("/ui")
public class CursorUserViewController {

  @GetMapping("/users")
  public String usersPage() {
    return "cursor";
  }
}
