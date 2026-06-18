package tutorials4j.framework.web.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.web.client.autoconfigure.ClientWebConfiguration;
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
 * 缓存请求体自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({
  ClientWebConfiguration.class,
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
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB] Web Auto Configuration");
  }
}
