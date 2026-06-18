package tutorials4j.framework.feature.schedule.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tutorials4j.framework.common.core.entity.YesNoEnum;
import tutorials4j.framework.common.core.util.ExceptionUtils;
import tutorials4j.framework.schedule.core.bean.ChangeStatusEvent;
import tutorials4j.framework.schedule.core.bean.TaskRuntimeData;
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
    TaskRuntimeData runtimeData = event.taskRuntimeData();
    jobRepository
        .findByTaskCode(runtimeData.taskCode())
        .ifPresentOrElse(
            task -> {
              JobLogEntity log = new JobLogEntity();
              log.setJob(task);
              log.setTaskStatus(runtimeData.taskStatus());
              log.setLotNo(runtimeData.lotNo());
              log.setTotalCount(runtimeData.totalCount());
              log.setTotalFailureCount(runtimeData.totalFailureCount());
              log.setStartTime(runtimeData.startTime());
              log.setEndTime(runtimeData.endTime());
              log.setHasError(YesNoEnum.N);
              log.setMessage(runtimeData.message());

              Throwable throwable = runtimeData.throwable();
              if (throwable != null) {
                log.setHasError(YesNoEnum.Y);
                log.setMessage(
                    StringUtils.substring(ExceptionUtils.getSelfStackTrace(throwable), 0, 500));
              }

              jobLogRepository.save(log);
            },
            () -> log.warn("任务不存在，无法处理事件，event={}", event));
  }
}
