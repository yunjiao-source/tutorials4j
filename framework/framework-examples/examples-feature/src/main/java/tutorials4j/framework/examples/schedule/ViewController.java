package tutorials4j.framework.examples.schedule;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class ViewController {
  @GetMapping("/manager")
  public String manager() {
    return "schedule/manager";
  }

  @GetMapping("/job-list")
  public String jobList() {
    return "schedule/job-list";
  }

  @GetMapping("/job-log-list")
  public String jobLogList() {
    return "schedule/job-log-list";
  }
}
