package tutorials4j.framework.assy.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.assy.core.autoconfigure.AssyConfiguration;

/**
 * 自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({AssyConfiguration.class})
public class AssyAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[ASSY] Assembly Auto Configuration");
  }
}
