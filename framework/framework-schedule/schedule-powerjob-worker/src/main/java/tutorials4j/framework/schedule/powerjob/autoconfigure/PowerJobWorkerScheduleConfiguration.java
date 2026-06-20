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
 * TODO
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
  @PostConstruct
  public void postConstruct() {
    log.trace("[SCHEDULE-POWERJOB-WORKER] PowerJob Worker Schedule Configuration");
  }

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
