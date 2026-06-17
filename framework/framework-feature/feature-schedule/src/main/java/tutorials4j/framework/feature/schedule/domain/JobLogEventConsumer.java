package tutorials4j.framework.feature.schedule.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.common.core.entity.YesNoEnum;
import tutorials4j.framework.common.core.util.ExceptionUtils;
import tutorials4j.framework.schedule.core.bean.ChangeStatusEvent;
import tutorials4j.framework.schedule.core.component.ChangeStatusEventConsumer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobLogEventConsumer implements ChangeStatusEventConsumer {
  private final JobLogRepository jobLogRepository;
  private final JobRepository jobRepository;

  @Override
  public void consumer(ChangeStatusEvent event) {
    jobRepository
        .findByTaskCode(event.taskCode())
        .ifPresentOrElse(
            task -> {
              JobLogEntity log = new JobLogEntity();
              log.setJob(task);
              log.setTaskStatus(event.taskStatus());
              log.setLotNo(event.taskRuntimeData().lotNo());
              log.setTotalCount(event.taskRuntimeData().totalCount());
              log.setTotalFailureCount(event.taskRuntimeData().totalFailureCount());
              log.setStartTime(event.taskRuntimeData().startTime());
              log.setEndTime(event.taskRuntimeData().endTime());
              log.setHasError(YesNoEnum.N);
              log.setMessage(event.taskRuntimeData().message());

              Throwable throwable = event.taskRuntimeData().throwable();
              if (throwable != null) {
                log.setHasError(YesNoEnum.Y);
                log.setMessage(ExceptionUtils.getSelfStackTrace(throwable).substring(0, 500));
              }

              jobLogRepository.save(log);
            },
            () -> log.warn("任务不存在，无法处理事件，event={}", event));
  }
}
