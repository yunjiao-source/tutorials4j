package tutorials4j.framework.web.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.web.core.component.GlobalWebExceptionHandler;

/**
 * Web 核心自动配置类。
 *
 * <p>注册全局 Web 异常处理器等核心组件，统一处理 Web 层抛出的异常。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WebConfiguration {
  /** 初始化日志记录。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-CORE] Web Core Configuration");
  }

  /**
   * 注册全局 Web 异常处理器 Bean。
   *
   * @return 全局 Web 异常处理器实例
   */
  @Bean
  GlobalWebExceptionHandler globalWebExceptionHandler() {
    log.trace("[WEB-CORE] Global Web Exception Handler");
    return new GlobalWebExceptionHandler();
  }
}
