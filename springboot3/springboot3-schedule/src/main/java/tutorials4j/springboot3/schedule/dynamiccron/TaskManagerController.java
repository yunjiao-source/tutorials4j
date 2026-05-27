package tutorials4j.springboot3.schedule.dynamiccron;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务管理
 *
 * @author yangyunjiao
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskManagerController {
  private final PrintTimeSchedulingConfigurer printTimeSchedulingConfigurer;

  @GetMapping("/print-time")
  public String printTime(@RequestParam("cron") String cron) {
    log.info("print-time new cron :{}", cron);
    printTimeSchedulingConfigurer.setCron(cron);
    return "ok";
  }
}
