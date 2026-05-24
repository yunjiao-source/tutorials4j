package tutorials4j.framework.assy.schedule.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.assy.core.properties.ScheduleAssyProperties;
import tutorials4j.framework.assy.schedule.TaskConfigRepository;
import tutorials4j.framework.assy.schedule.YamlTaskConfigRepository;

/**
 * 缓存核心配置类。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ScheduleAssyProperties.class})
public class ScheduleAssyConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[ASSY-CORE] Schedule Assy Configuration");
  }

  @Bean
  TaskConfigRepository YamlTaskConfigRepository(ScheduleAssyProperties properties) {
    log.debug("[ASSY-CORE] Yaml Task Config Repository");
    return new YamlTaskConfigRepository(properties.getTasks());
  }
}
