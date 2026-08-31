package tutorials4j.framework.oss.fss.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.oss.fss.component.LogFileStorageAspect;

/**
 * x-file-storage-spring 的自动配置类。
 *
 * <p>启用文件存储功能（{@link EnableFileStorage}），并注册日志切面 {@link LogFileStorageAspect}。
 *
 * @author Yun Jiao
 */
@Slf4j
@EnableFileStorage
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({})
public class FileStorageSpringConfiguration {

  @PostConstruct
  public void postConstruct() {
    log.trace("[OSS-SPRING-FILE-STORAGE] Spring File Storage Configuration");
  }

  /**
   * 注册日志切面 Bean，用于记录文件操作的调试日志。
   *
   * @return 日志切面实例
   */
  @Bean
  LogFileStorageAspect logFileStorageAspect() {
    log.trace("[OSS-SPRING-FILE-STORAGE] Log File Storage Aspect");
    return new LogFileStorageAspect();
  }
}
