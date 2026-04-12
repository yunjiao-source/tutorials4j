package tutorials4j.springboot3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

/**
 * 日志
 *
 * @author Yun Jiao
 */
@Slf4j
public class LogStepExecutionListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("Starting step: {}", stepExecution);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info(String.format(
                "Step completed: Read=%d, Write=%d, Skip=%d",
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount()
        ));
        return stepExecution.getExitStatus();
    }
}
