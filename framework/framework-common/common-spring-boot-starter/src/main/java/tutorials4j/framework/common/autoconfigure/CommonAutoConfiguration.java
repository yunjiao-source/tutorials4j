package tutorials4j.framework.common.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.common.spring.autoconfigure.JsonCommonConfiguration;
import tutorials4j.framework.common.spring.autoconfigure.SpringCommonConfiguration;
import tutorials4j.framework.common.uid.autoconfigure.UidCommonConfiguration;

/**
 * 通用模块自动配置入口。
 *
 * <p>通过 {@link Import} 统一导入 Spring 通用配置、JSON 通用配置与 UID 通用配置， 供框架使用者一键启用通用模块的自动装配能力。
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
  /** 启动后打印通用模块自动配置的初始化日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[COMMON] Common Auto Configuration");
  }
}
