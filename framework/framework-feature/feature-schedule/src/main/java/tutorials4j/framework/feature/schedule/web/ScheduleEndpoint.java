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
 * 调度控制接口。
 *
 * <p>提供定时任务的启动、取消与执行详情查询等运行时控制能力。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleEndpoint {
  private final ScheduleService scheduleService;

  /**
   * 取消指定任务的执行。
   *
   * @param taskCode 任务编码
   * @return 取消后的任务执行详情
   */
  @GetMapping("cancel")
  public Result<TaskExecutionDetails> cancelTask(@RequestParam("taskCode") String taskCode) {
    return Result.success(scheduleService.cancelTask(taskCode));
  }

  /**
   * 启动指定任务。
   *
   * @param taskCode 任务编码
   * @return 启动后的任务执行详情
   */
  @GetMapping("start")
  public Result<TaskExecutionDetails> startTask(@RequestParam("taskCode") String taskCode) {
    return Result.success(scheduleService.startTask(taskCode));
  }

  /**
   * 查询指定任务的执行详情。
   *
   * @param taskCode 任务编码
   * @return 任务执行详情
   */
  @GetMapping("details")
  public Result<TaskExecutionDetails> getTaskDetails(@RequestParam("taskCode") String taskCode) {
    return Result.success(scheduleService.getTaskDetails(taskCode));
  }

  /**
   * 查询所有任务的执行详情。
   *
   * @return 所有任务执行详情列表
   */
  @GetMapping("all")
  public Result<List<TaskExecutionDetails>> getAll() {
    return Result.success(scheduleService.getAllTaskDetails());
  }
}
