package tutorials4j.springboot3.qps;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 前端页面控制器
 *
 * @author Yun Jiao
 */
@Controller
public class MethodQpsMonitorController {

  private final MethodQpsAspect methodQpsAspect;

  public MethodQpsMonitorController(MethodQpsAspect methodQpsAspect) {
    this.methodQpsAspect = methodQpsAspect;
  }

  @GetMapping("/monitor/qps/method")
  public String qpsMonitorPage(Model model) {
    Map<String, MethodCallStats> stats = methodQpsAspect.getAllRecentCallStats();
    model.addAttribute("stats", stats);
    return "method-qps-monitor";
  }
}
