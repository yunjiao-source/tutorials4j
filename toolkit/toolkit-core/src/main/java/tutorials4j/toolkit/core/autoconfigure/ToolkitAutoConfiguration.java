package tutorials4j.toolkit.core.autoconfigure;

import cn.hutool.extra.spring.SpringUtil;
import io.micrometer.tracing.Tracer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import tutorials4j.toolkit.core.concurrent.ScheduledThreadPoolExecutorHolder;
import tutorials4j.toolkit.core.concurrent.ThreadPoolExecutorHolder;
import tutorials4j.toolkit.core.web.GlobalWebExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({SpringUtil.class})
@EnableConfigurationProperties({
  ToolkitProperties.class,
})
public class ToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT] Toolkit Auto Configuration");
  }

  @Bean(destroyMethod = "shutdown")
  ScheduledThreadPoolExecutorHolder scheduledThreadPoolExecutorHolder(
      ToolkitProperties properties) {
    log.trace("[TOOLKIT] Scheduled Thread Pool Executor Holder");
    ScheduledThreadPoolExecutorHolder.instance.initExecutor(
        properties.getScheduledThreadPoolExecutor());
    return ScheduledThreadPoolExecutorHolder.instance;
  }

  @Bean(destroyMethod = "shutdown")
  ThreadPoolExecutorHolder threadPoolExecutorHolder(ToolkitProperties properties) {
    log.trace("[TOOLKIT] Thread Pool Executor Holder");
    ThreadPoolExecutorHolder.instance.initExecutor(properties.getThreadPoolExecutor());
    return ThreadPoolExecutorHolder.instance;
  }

  @Bean
  GlobalWebExceptionHandler GlobalWebExceptionHandler(Tracer tracer) {
    log.trace("[TOOLKIT - WEB] Global Web Exception Handler");
    return new GlobalWebExceptionHandler(tracer);
  }
}
