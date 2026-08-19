package tutorials4j.framework.feature.schedule.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.feature.schedule.domain.JobLogEntity;
import tutorials4j.framework.feature.schedule.domain.JobLogQuery;
import tutorials4j.framework.feature.schedule.domain.JobLogService;

/**
 * 任务执行日志查询接口。
 *
 * <p>提供定时任务执行日志的分页查询能力。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/job-log")
@RequiredArgsConstructor
public class JobLogEndpoint {
  private final JobLogService jobLogService;

  /**
   * 分页查询任务执行日志。
   *
   * @param query 查询条件
   * @param pageable 分页参数
   * @return 任务执行日志视图对象分页结果
   */
  @GetMapping("page")
  public Result<PagedModel<JobLogVO>> findPage(JobLogQuery query, Pageable pageable) {
    Page<JobLogEntity> page = jobLogService.find(query, pageable);
    return Result.success(new PagedModel<>(page.map(JobLogVO::of)));
  }
}
