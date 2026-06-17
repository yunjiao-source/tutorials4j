package tutorials4j.framework.feature.schedule.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.feature.schedule.domain.JobLogEntity;
import tutorials4j.framework.feature.schedule.domain.JobLogQuery;
import tutorials4j.framework.feature.schedule.domain.JobLogService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/job-log")
@RequiredArgsConstructor
public class JobLogEndpoint {
  private final JobLogService jobLogService;

  @GetMapping("page")
  public ResponseEntity<PagedModel<JobLogVO>> findPage(JobLogQuery query, Pageable pageable) {
    Page<JobLogEntity> page = jobLogService.find(query, pageable);
    return ResponseEntity.ok(new PagedModel<>(page.map(JobLogVO::of)));
  }
}
