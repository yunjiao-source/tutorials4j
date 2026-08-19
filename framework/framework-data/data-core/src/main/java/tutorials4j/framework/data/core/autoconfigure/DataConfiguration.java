package tutorials4j.framework.data.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.data.core.properties.DataProperties;

/**
 * Data 模块核心配置类。
 *
 * <p>启用 Data 核心配置属性（{@link DataProperties}）的绑定，作为 Data 模块各 数据访问组件配置的基础。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({DataProperties.class})
public class DataConfiguration {
  /** 启动后打印 Data 核心配置的初始化日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[DATA-CORE] Data Configuration");
  }
}
