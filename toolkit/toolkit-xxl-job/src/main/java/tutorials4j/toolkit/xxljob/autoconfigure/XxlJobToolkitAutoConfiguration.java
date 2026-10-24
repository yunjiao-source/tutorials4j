package tutorials4j.toolkit.xxljob.autoconfigure;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tutorials4j.toolkit.xxljob.support.XxlJobSpringExecutorCustomizer;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({
  XxlJobProperties.class,
})
public class XxlJobToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-XXL-JOB] Xxl Job Toolkit Auto Configuration");
  }

  @Bean
  public XxlJobSpringExecutor xxlJobSpringExecutor(
      XxlJobProperties properties, ObjectProvider<XxlJobSpringExecutorCustomizer> customizers) {
    log.trace("[TOOLKIT-XXL-JOB] Xxl Job Spring Executor");
    XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
    executor.setAdminAddresses(properties.getAdmin().getAddresses());
    executor.setTimeout((int) properties.getAdmin().getTimeout().toSeconds());
    executor.setAppname(properties.getExecutor().getAppName());
    executor.setIp(properties.getExecutor().getIp());
    executor.setPort(properties.getExecutor().getPort());
    executor.setAccessToken(properties.getExecutor().getAccessToken());
    executor.setLogPath(properties.getExecutor().getLogPath());
    executor.setLogRetentionDays(properties.getExecutor().getLogRetentionDays().getDays());
    executor.setEnabled(properties.getExecutor().isEnabled());

    customizers.stream().sorted().forEach(customizer -> customizer.customize(executor));
    return executor;
  }
}
