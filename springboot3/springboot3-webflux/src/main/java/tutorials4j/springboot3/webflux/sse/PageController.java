package tutorials4j.springboot3.webflux.sse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

  @GetMapping
  public String index(Model model) {
    return "sse/index";
  }

  @GetMapping("/news")
  public String news(Model model) {
    model.addAttribute("userId", "user123"); // 动态数据
    return "sse/news";
  }

  @GetMapping("/notifications")
  public String notifications(Model model) {
    // 实际开发中从SecurityContext或session获取当前用户ID
    String currentUserId = "user123";
    model.addAttribute("userId", currentUserId);
    return "sse/notifications";
  }
}
