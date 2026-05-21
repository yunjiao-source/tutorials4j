package tutorials4j.framework.common.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.common.json.autoconfigure.JsonCommonConfiguration;
import tutorials4j.framework.common.spring.autoconfigure.SpringCommonConfiguration;
import tutorials4j.framework.common.uid.autoconfigure.UidCommonConfiguration;

/**
 * 通用模块自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({
  SpringCommonConfiguration.class,
  JsonCommonConfiguration.class,
  UidCommonConfiguration.class
})
public class CommonAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[COMMON] Common Auto Configuration");
  }
}
