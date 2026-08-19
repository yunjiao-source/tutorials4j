package tutorials4j.framework.feature.schedule.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.feature.schedule.domain.JobEntity;
import tutorials4j.framework.feature.schedule.domain.JobQuery;
import tutorials4j.framework.feature.schedule.domain.JobService;
import tutorials4j.framework.schedule.core.exception.ScheduleErrorCode;
import tutorials4j.framework.schedule.spring.component.ScheduleTaskManager;

/**
 * 定时任务管理接口。
 *
 * <p>提供定时任务的创建、更新、删除与分页查询能力；对正在运行的任务进行更新或删除时会抛出异常。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobEndpoint {
  private final JobService jobService;
  private final ScheduleTaskManager scheduleTaskManager;

  /**
   * 更新定时任务。
   *
   * @param id 任务 ID
   * @param dto 更新请求参数
   * @return 更新后的定时任务视图对象
   */
  @PutMapping("/{id}")
  public Result<JobVO> updateJob(
      @PathVariable("id") Long id, @Valid @RequestBody JobUpdateDTO dto) {
    JobEntity job = jobService.findById(id);
    checkJobIsRunning(job.getTaskCode());

    BeanUtils.copyProperties(dto, job);
    JobEntity updated = jobService.save(job);
    return Result.success(JobVO.of(updated));
  }

  /**
   * 新建定时任务。
   *
   * @param dto 创建请求参数
   * @return 创建后的定时任务视图对象
   */
  @PostMapping
  public Result<JobVO> save(@Valid @RequestBody JobCreateDTO dto) {
    JobEntity job = new JobEntity();
    BeanUtils.copyProperties(dto, job);
    JobEntity created = jobService.save(job);
    return Result.success(JobVO.of(created));
  }

  /**
   * 删除定时任务。
   *
   * @param id 任务 ID
   * @return 无内容响应
   */
  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable("id") Long id) {
    JobEntity job = jobService.findById(id);
    checkJobIsRunning(job.getTaskCode());

    jobService.delete(job);
    return Result.success();
  }

  /**
   * 分页查询定时任务。
   *
   * @param query 查询条件
   * @param pageable 分页参数
   * @return 定时任务视图对象分页结果
   */
  @GetMapping("page")
  public Result<PagedModel<JobVO>> findPage(JobQuery query, Pageable pageable) {
    Page<JobEntity> page = jobService.find(query, pageable);
    return Result.success(new PagedModel<>(page.map(JobVO::of)));
  }

  /**
   * 校验任务是否正在运行，若正在运行则抛出异常。
   *
   * @param taskCode 任务编码
   */
  private void checkJobIsRunning(String taskCode) {
    if (scheduleTaskManager.isTaskRunning(taskCode)) {
      throw ScheduleErrorCode.SCHEDULE_JOB_IS_RUNNING.throwed().param("taskCode", taskCode);
    }
  }
}
