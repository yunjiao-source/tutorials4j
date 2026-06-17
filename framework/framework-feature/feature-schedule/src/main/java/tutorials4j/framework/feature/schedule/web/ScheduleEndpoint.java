package tutorials4j.framework.feature.schedule.web;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.feature.schedule.ScheduleService;
import tutorials4j.framework.feature.schedule.TaskExecutionDetails;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleEndpoint {
  private final ScheduleService scheduleService;

  @GetMapping("cancel")
  public TaskExecutionDetails cancelTask(@RequestParam("taskCode") String taskCode) {
    return scheduleService.cancelTask(taskCode);
  }

  @GetMapping("start")
  public TaskExecutionDetails startTask(@RequestParam("taskCode") String taskCode) {
    return scheduleService.startTask(taskCode);
  }

  @GetMapping("details")
  public TaskExecutionDetails getTaskDetails(@RequestParam("taskCode") String taskCode) {
    return scheduleService.getTaskDetails(taskCode);
  }

  @GetMapping("all")
  public List<TaskExecutionDetails> getAll() {
    return scheduleService.getAllTaskDetails();
  }
}
