package tutorials4j.springboot3.mdc;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步任务支持
 *
 * @author Yun Jiao
 */
@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean
  public ThreadPoolTaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(50);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("async-");
    executor.setTaskDecorator(new MdcTaskDecorator());
    executor.initialize();
    return executor;
  }

  @Slf4j
  public static class MdcTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
      // 复制当前线程的MDC上下文
      Map<String, String> contextMap = MDC.getCopyOfContextMap();
      return () -> {
        try {
          // 异步任务执行前设置MDC
          if (contextMap != null) {
            MDC.setContextMap(contextMap);
          }
          log.info("MdcTaskDecorator添加追踪信息");
          runnable.run();
        } finally {
          MDC.clear();
        }
      };
    }
  }
}
