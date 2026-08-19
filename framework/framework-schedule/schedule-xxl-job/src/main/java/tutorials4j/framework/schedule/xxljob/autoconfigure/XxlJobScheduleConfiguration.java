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
 * XXL-JOB 调度自动配置类。
 *
 * <p>当配置项 {@code tutorials4j.schedule.xxl-job.enabled} 为 true 时生效，负责创建并配置 {@link
 * XxlJobSpringExecutor} 执行器 Bean，将执行器注册到调度中心，并应用所有 {@link XxlJobSpringExecutorCustomizer} 定制器。
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
  /** 初始化日志记录。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[SCHEDULE-XXL-JOB] Xxl Job Schedule Configuration");
  }

  /**
   * 创建并配置 XXL-JOB 执行器 Bean。
   *
   * <p>根据 {@link XxlJobProperties} 填充执行器的调度中心地址、超时时间、执行器名称、端口、 访问令牌、日志路径等配置，并按顺序应用所有定制器。
   *
   * @param properties XXL-JOB 配置属性
   * @param customizers 可选的执行器定制器列表
   * @return 配置完成的 {@link XxlJobSpringExecutor} 实例
   */
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
