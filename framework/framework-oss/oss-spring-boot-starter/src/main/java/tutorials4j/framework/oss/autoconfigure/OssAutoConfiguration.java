package tutorials4j.framework.oss.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.oss.core.autoconfigure.OssConfiguration;
import tutorials4j.framework.oss.fss.autoconfigure.FileStorageSpringConfiguration;
import tutorials4j.framework.oss.fss.autoconfigure.FileStorageSpringMetricsConfiguration;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({
  OssConfiguration.class,
  FileStorageSpringConfiguration.class,
  FileStorageSpringMetricsConfiguration.class
})
public class OssAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[OSS] OSS Auto Configuration");
  }
}
