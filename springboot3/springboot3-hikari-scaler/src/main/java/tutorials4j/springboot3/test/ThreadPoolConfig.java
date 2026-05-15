package tutorials4j.springboot3.test;

import java.util.concurrent.ExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 线程池配置
 *
 * @author Yun Jiao
 */
@Configuration
public class ThreadPoolConfig {

  @Bean(destroyMethod = "shutdown") // 确保应用关闭时线程池终止
  public ExecutorService customExecutorService() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(100);
    executor.setMaxPoolSize(200);
    executor.setQueueCapacity(200);
    executor.setThreadNamePrefix("test-");
    executor.initialize(); // 初始化线程池
    return executor.getThreadPoolExecutor(); // 返回 ExecutorService 实例
  }
}
