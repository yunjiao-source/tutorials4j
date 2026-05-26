package tutorials4j.springboot3.batch.app;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import tutorials4j.springboot3.common.JpaCommonConfiguration;

/**
 * 批处理配置
 *
 * @author Yun Jiao
 */
@Profile("simple")
@Configuration
@EnableScheduling
@EnableBatchProcessing
@RequiredArgsConstructor
@Import(JpaCommonConfiguration.class)
@ComponentScan(basePackages = {"tutorials4j.springboot3.batch.simple"})
public class SimpleConfig {
  private final JobRepository jobRepository;

  @Bean
  public JobLauncher jobLauncher() throws Exception {
    TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
    jobLauncher.setJobRepository(jobRepository);
    jobLauncher.setTaskExecutor(taskExecutor());
    jobLauncher.afterPropertiesSet();
    return jobLauncher;
  }

  private TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(3);
    executor.setMaxPoolSize(6);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("batch-");
    executor.setKeepAliveSeconds(60);
    executor.initialize();
    return executor;
  }
}
