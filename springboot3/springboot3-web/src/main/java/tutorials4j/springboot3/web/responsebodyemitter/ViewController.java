package tutorials4j.springboot3.web.responsebodyemitter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Controller
public class ViewController {

  @GetMapping("/stream-ui/logs")
  public String logsPage() {
    return "responsebodyemitter/stream-logs";
  }

  @GetMapping("/stream-ui/progress")
  public String progressPage() {
    return "responsebodyemitter/stream-progress";
  }

  @GetMapping("/stream-ui/realtime")
  public String realtimePage() {
    return "responsebodyemitter/stream-realtime";
  }
}
