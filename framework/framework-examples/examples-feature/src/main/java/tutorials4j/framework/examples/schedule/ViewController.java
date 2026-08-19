package tutorials4j.framework.examples.schedule;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 定时任务管理示例的页面控制器，提供任务管理、任务列表与执行日志列表页面的跳转。
 *
 * @author Yun Jiao
 */
@Controller
public class ViewController {
  /**
   * 跳转到任务管理页面。
   *
   * @return 视图名称
   */
  @GetMapping("/manager")
  public String manager() {
    return "schedule/manager";
  }

  /**
   * 跳转到任务列表页面。
   *
   * @return 视图名称
   */
  @GetMapping("/job-list")
  public String jobList() {
    return "schedule/job-list";
  }

  /**
   * 跳转到执行日志列表页面。
   *
   * @return 视图名称
   */
  @GetMapping("/job-log-list")
  public String jobLogList() {
    return "schedule/job-log-list";
  }
}
