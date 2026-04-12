package tutorials4j.springboot3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**
 * 日志监听器
 *
 * @author Yun Jiao
 */
@Slf4j
public class LogJobExecutionListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("!!! BEFORE JOB: {}.", jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("!!! AFTER JOB: {}.", jobExecution);
    }
}

