package tutorials4j.framework.feature.schedule.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 调度功能自动配置类。
 *
 * <p>扫描调度功能的领域与 Web 组件，并启用对应包下的 JPA 仓库与实体扫描。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ComponentScan(
    basePackages = {
      "tutorials4j.framework.feature.schedule.domain",
      "tutorials4j.framework.feature.schedule.web"
    })
@EnableJpaRepositories(basePackages = {"tutorials4j.framework.feature.schedule.domain"})
@EntityScan(basePackages = {"tutorials4j.framework.feature.schedule.domain"})
public class ScheduleFeatureConfiguration {
  /** 初始化：输出调度功能配置已加载的跟踪日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[FEATURE-SCHEDULE] Schedule Feature Configuration");
  }
}
