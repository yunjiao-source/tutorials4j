package tutorials4j.framework.feature.schedule.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tutorials4j.framework.common.core.entity.YesNoEnum;
import tutorials4j.framework.common.core.util.ExceptionUtils;
import tutorials4j.framework.schedule.core.bean.TaskRuntimeData;
import tutorials4j.framework.schedule.core.component.TaskRuntimeDataHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseTaskRuntimeDataHandler implements TaskRuntimeDataHandler {
  private final JobLogRepository jobLogRepository;
  private final JobRepository jobRepository;

  @Async
  @Override
  public void handle(TaskRuntimeData data) {
    jobRepository
        .findByTaskCode(data.taskCode())
        .ifPresentOrElse(
            task -> {
              JobLogEntity log = new JobLogEntity();
              log.setJob(task);
              log.setTaskStatus(data.taskStatus());
              log.setLotNo(data.lotNo());
              log.setTotalCount(data.totalCount());
              log.setTotalFailureCount(data.totalFailureCount());
              log.setStartTime(data.startTime());
              log.setEndTime(data.endTime());
              log.setHasError(YesNoEnum.N);
              log.setMessage(data.message());

              Throwable throwable = data.throwable();
              if (throwable != null) {
                log.setHasError(YesNoEnum.Y);
                log.setMessage(
                    StringUtils.substring(ExceptionUtils.getSelfStackTrace(throwable), 0, 500));
              }

              jobLogRepository.save(log);
            },
            () -> log.warn("任务不存在，无法处理事件，data={}", data));
  }
}
