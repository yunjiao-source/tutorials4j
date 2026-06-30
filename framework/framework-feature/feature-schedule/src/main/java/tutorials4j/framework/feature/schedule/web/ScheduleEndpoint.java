package tutorials4j.framework.feature.schedule.web;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.schedule.spring.bean.TaskExecutionDetails;
import tutorials4j.framework.schedule.spring.component.ScheduleService;

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
  public Result<TaskExecutionDetails> cancelTask(@RequestParam("taskCode") String taskCode) {
    return Result.success(scheduleService.cancelTask(taskCode));
  }

  @GetMapping("start")
  public Result<TaskExecutionDetails> startTask(@RequestParam("taskCode") String taskCode) {
    return Result.success(scheduleService.startTask(taskCode));
  }

  @GetMapping("details")
  public Result<TaskExecutionDetails> getTaskDetails(@RequestParam("taskCode") String taskCode) {
    return Result.success(scheduleService.getTaskDetails(taskCode));
  }

  @GetMapping("all")
  public Result<List<TaskExecutionDetails>> getAll() {
    return Result.success(scheduleService.getAllTaskDetails());
  }
}
