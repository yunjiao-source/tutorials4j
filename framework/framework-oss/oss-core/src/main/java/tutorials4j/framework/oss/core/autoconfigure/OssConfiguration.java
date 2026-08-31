package tutorials4j.framework.oss.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OSS 核心自动配置类。
 *
 * <p>启用 {@link OssProperties} 配置绑定，并输出启动日志。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({OssProperties.class})
public class OssConfiguration {

  @PostConstruct
  public void postConstruct() {
    log.trace("[OSS-CORE] OSS Configuration");
  }
}
