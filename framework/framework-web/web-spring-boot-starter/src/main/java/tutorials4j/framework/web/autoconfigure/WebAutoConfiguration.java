package tutorials4j.framework.web.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.web.core.autoconfigure.WebConfiguration;
import tutorials4j.framework.web.flux.autoconfigure.ClientWebConfiguration;
import tutorials4j.framework.web.flux.autoconfigure.FluxWebConfiguration;
import tutorials4j.framework.web.logging.autoconfigure.RequestLoggingWebConfiguration;
import tutorials4j.framework.web.logging.autoconfigure.SpringdocWebConfiguration;
import tutorials4j.framework.web.logging.autoconfigure.TraceWebConfiguration;
import tutorials4j.framework.web.rest.autoconfigure.CachedBodyConfiguration;
import tutorials4j.framework.web.security.autoconfigure.SecurityWebConfiguration;
import tutorials4j.framework.web.security.autoconfigure.SignatureWebConfiguration;
import tutorials4j.framework.web.security.autoconfigure.TotpWebConfiguration;
import tutorials4j.framework.web.security.autoconfigure.XssWebConfiguration;
import tutorials4j.framework.web.validation.autoconfigure.ValidatorsWebConfiguration;

/**
 * Web 模块自动配置入口，导入 Web 核心、客户端、日志、安全、校验等各子模块配置。
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({
  WebConfiguration.class,
  ClientWebConfiguration.class,
  FluxWebConfiguration.class,
  RequestLoggingWebConfiguration.class,
  SpringdocWebConfiguration.class,
  TraceWebConfiguration.class,
  CachedBodyConfiguration.class,
  SecurityWebConfiguration.class,
  SignatureWebConfiguration.class,
  TotpWebConfiguration.class,
  XssWebConfiguration.class,
  ValidatorsWebConfiguration.class,
})
public class WebAutoConfiguration {
  /** 初始化日志输出。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB] Web Auto Configuration");
  }
}
