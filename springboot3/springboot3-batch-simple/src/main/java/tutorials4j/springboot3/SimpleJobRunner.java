package tutorials4j.springboot3;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 触发Job执行的几种方式
 *
 * @author Yun Jiao
 */
@Component
@RestController
@RequiredArgsConstructor
public class SimpleJobRunner implements CommandLineRunner {
    private final JobLauncher jobLauncher;
    private final Job importUserJob;
    private final JobRepository jobRepository;

    /**
     * 使用CommandLineRunner（应用启动时执行）
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(importUserJob, jobParameters);
    }

    /**
     * 方式2：使用Scheduled任务（定时执行）
     *
     * @throws Exception
     */

    @Scheduled(initialDelay = 30000, fixedDelay = 60000)
    public void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .addString("jobName", importUserJob.getName())
                .toJobParameters();

        jobLauncher.run(importUserJob, jobParameters);
    }

    /**
     * 方式3：通过REST API触发（推荐）
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/import-users")
    public ResponseEntity<String> launchJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(importUserJob, jobParameters);

        return ResponseEntity.ok(
                String.format("Job started with ID: %d", jobExecution.getId())
        );
    }
    @GetMapping("/status/{jobExecutionId}")
    public ResponseEntity<JobExecution> getJobStatus(
            @PathVariable Long jobExecutionId) {
        JobExecution jobExecution = jobRepository.getLastJobExecution(
                "importUserJob",
                new JobParameters()
        );

        return ResponseEntity.ok(jobExecution);
    }
}
