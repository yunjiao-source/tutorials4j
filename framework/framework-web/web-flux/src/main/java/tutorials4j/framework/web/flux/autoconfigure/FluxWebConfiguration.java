package tutorials4j.framework.web.flux.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.web.flux.component.GlobalWebFluxExceptionHandler;

/**
 * WebFlux 全局组件的自动配置类。
 *
 * <p>负责注册 WebFlux 全局异常处理器等通用组件。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class FluxWebConfiguration {
  /** 初始化日志输出，应用启动后执行。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-CLIENT] Flux Web Configuration");
  }

  /**
   * 注册 WebFlux 全局异常处理器 Bean。
   *
   * @return 全局 WebFlux 异常处理器实例
   */
  @Bean
  GlobalWebFluxExceptionHandler globalWebFluxValidationExceptionHandler() {
    log.trace("[WEB-CLIENT] Global Web Flux Exception Handler");
    return new GlobalWebFluxExceptionHandler();
  }
}
