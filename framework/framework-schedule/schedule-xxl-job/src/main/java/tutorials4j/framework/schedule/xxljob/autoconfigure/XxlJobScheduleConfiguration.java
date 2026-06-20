package tutorials4j.framework.schedule.xxljob.autoconfigure;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.schedule.xxljob.XxlJobProperties;
import tutorials4j.framework.schedule.xxljob.XxlJobSpringExecutorCustomizer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
  XxlJobProperties.class,
})
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_SCHEDULE_XXL_JOB,
    name = PropertiesConsts.PROPERTY_ENABLED)
public class XxlJobScheduleConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[SCHEDULE-XXL-JOB] Xxl Job Schedule Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  public XxlJobSpringExecutor xxlJobSpringExecutor(
      XxlJobProperties properties, ObjectProvider<XxlJobSpringExecutorCustomizer> customizers) {
    log.trace("[SCHEDULE-XXL-JOB] Xxl Job Spring Executor");
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
