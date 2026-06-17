package tutorials4j.framework.feature.schedule.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.feature.schedule.domain.JobEntity;
import tutorials4j.framework.feature.schedule.domain.JobQuery;
import tutorials4j.framework.feature.schedule.domain.JobService;
import tutorials4j.framework.schedule.core.component.ScheduleTaskManager;
import tutorials4j.framework.schedule.core.exception.ScheduleException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobEndpoint {
  private final JobService jobService;
  private final ScheduleTaskManager scheduleTaskManager;

  @PutMapping("/{id}")
  public ResponseEntity<JobVO> updateJob(
      @PathVariable("id") Long id, @RequestBody JobUpdateDTO dto) {
    JobEntity job = jobService.findById(id);
    checkJobIsRunning(job.getTaskCode());

    BeanUtils.copyProperties(dto, job);
    JobEntity updated = jobService.save(job);
    return ResponseEntity.ok(JobVO.of(updated));
  }

  @PostMapping
  public ResponseEntity<JobVO> save(@RequestBody JobCreateDTO dto) {
    JobEntity job = new JobEntity();
    BeanUtils.copyProperties(dto, job);
    JobEntity created = jobService.save(job);
    return new ResponseEntity<>(JobVO.of(created), HttpStatus.CREATED);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
    JobEntity job = jobService.findById(id);
    checkJobIsRunning(job.getTaskCode());

    jobService.delete(job);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("page")
  public ResponseEntity<PagedModel<JobVO>> findPage(JobQuery query, Pageable pageable) {
    Page<JobEntity> page = jobService.find(query, pageable);
    return ResponseEntity.ok(new PagedModel<>(page.map(JobVO::of)));
  }

  private void checkJobIsRunning(String taskCode) {
    if (scheduleTaskManager.isTaskRunning(taskCode)) {
      throw new ScheduleException("任务运行中，不能操作；先取消任务，然后再尝试本操作");
    }
  }
}
