package tutorials4j.framework.schedule.powerjob.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.powerjob.worker.PowerJobSpringWorker;
import tech.powerjob.worker.common.PowerJobWorkerConfig;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.schedule.powerjob.PowerJobWorkerConfigCustomizer;
import tutorials4j.framework.schedule.powerjob.PowerJobWorkerProperties;

/**
 * PowerJob Worker 调度自动配置类。
 *
 * <p>当 {@code tutorials4j.schedule.powerjob.worker.enabled} 配置为 {@code true} 时生效， 根据属性配置构建 {@link
 * PowerJobSpringWorker} Worker 实例，并允许通过定制器调整 Worker 配置。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
  PowerJobWorkerProperties.class,
})
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_SCHEDULE_POWERJOB_WORKER,
    name = PropertiesConsts.PROPERTY_ENABLED)
public class PowerJobWorkerScheduleConfiguration {
  /** 初始化：输出 PowerJob Worker 调度配置已加载的跟踪日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[SCHEDULE-POWERJOB-WORKER] PowerJob Worker Schedule Configuration");
  }

  /**
   * 注册 PowerJob Spring Worker 实例。
   *
   * <p>将属性配置转换为 {@link PowerJobWorkerConfig}，并依次应用所有定制的 {@link PowerJobWorkerConfigCustomizer}。
   *
   * @param properties PowerJob Worker 属性
   * @param customizers Worker 配置定制器提供者
   * @return PowerJob Spring Worker 实例
   */
  @Bean
  @ConditionalOnMissingBean
  public PowerJobSpringWorker powerJobSpringWorker(
      PowerJobWorkerProperties properties,
      ObjectProvider<PowerJobWorkerConfigCustomizer> customizers) {
    log.trace("[SCHEDULE-POWERJOB-WORKER] Power Job Spring Worker");
    PowerJobWorkerConfig config = new PowerJobWorkerConfig();
    config.setAppName(properties.getAppName());
    config.setPort(properties.getPort());
    config.setServerAddress(properties.getServerAddress());
    config.setStoreStrategy(properties.getStoreStrategy());
    config.setProtocol(properties.getProtocol());
    config.setMaxResultLength(properties.getMaxResultLength());
    config.setUserContext(properties.getUserContext());
    config.setAllowLazyConnectServer(properties.isAllowLazyConnectServer());
    config.setMaxAppendedWfContextLength(properties.getMaxAppendedWfContextLength());
    config.setMaxLightweightTaskNum(properties.getMaxLightweightTaskNum());
    config.setMaxHeavyweightTaskNum(properties.getMaxHeavyweightTaskNum());
    config.setHealthReportInterval((int) properties.getHealthReportInterval().toSeconds());
    config.setTag(properties.getTag());

    customizers.stream().sorted().forEach(customizer -> customizer.customize(config));
    return new PowerJobSpringWorker(config);
  }
}
