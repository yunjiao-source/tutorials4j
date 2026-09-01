package tutorials4j.framework.feature.oss.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import tutorials4j.framework.feature.oss.annotation.EnableFileStorageSpringFeature;

/**
 * 文件存储Spring特性的自动配置类。
 *
 * <p>通过{@code @ComponentScan}扫描{@code tutorials4j.framework.feature.oss.fss}包下的组件，
 * 并通过{@code @EnableJpaRepositories}和{@code @EntityScan}启用JPA仓库和实体扫描。 该配置由{@link
 * EnableFileStorageSpringFeature}注解触发导入。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = {"tutorials4j.framework.feature.oss.fss"})
@EnableJpaRepositories(basePackages = {"tutorials4j.framework.feature.oss.fss"})
@EntityScan(basePackages = {"tutorials4j.framework.feature.oss.fss"})
public class FileStorageSpringFeatureConfiguration {

  /** 配置加载完成后输出日志，便于追踪特性是否被激活。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[FEATURE-OSS] File Storage Spring Feature Configuration");
  }
}
