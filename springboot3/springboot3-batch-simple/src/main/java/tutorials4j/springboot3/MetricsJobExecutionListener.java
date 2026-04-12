package tutorials4j.springboot3;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 监控指标监听器
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class MetricsJobExecutionListener implements JobExecutionListener {

    private final MeterRegistry meterRegistry;
    private LocalDateTime startTime;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        startTime = LocalDateTime.now();
        meterRegistry.counter("batch.job.start",
                "jobName", jobExecution.getJobInstance().getJobName()).increment();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);

        // 记录作业执行时间
        meterRegistry.timer("batch.job.duration",
                        "jobName", jobExecution.getJobInstance().getJobName(),
                        "status", jobExecution.getStatus().toString())
                .record(duration);

        // 记录各种指标
        meterRegistry.counter("batch.job.completed",
                        "jobName", jobExecution.getJobInstance().getJobName(),
                        "status", jobExecution.getStatus().toString())
                .increment();

        meterRegistry.gauge("batch.job.read.count",
                jobExecution.getStepExecutions().stream()
                        .mapToLong(step -> step.getReadCount())
                        .sum());

        meterRegistry.gauge("batch.job.write.count",
                jobExecution.getStepExecutions().stream()
                        .mapToLong(step -> step.getWriteCount())
                        .sum());

        meterRegistry.gauge("batch.job.skip.count",
                jobExecution.getStepExecutions().stream()
                        .mapToLong(step -> step.getSkipCount())
                        .sum());
    }
}
