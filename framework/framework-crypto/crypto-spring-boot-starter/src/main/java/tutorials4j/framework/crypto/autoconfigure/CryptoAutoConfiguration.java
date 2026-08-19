package tutorials4j.framework.crypto.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.crypto.core.autoconfigure.CryptoConfiguration;
import tutorials4j.framework.crypto.hutool.autoconfigure.HutoolCryptoConfiguration;
import tutorials4j.framework.crypto.web.autoconfigure.CryptoWebConfiguration;

/**
 * 框架加密模块的 Spring Boot 自动配置入口。
 *
 * <p>通过 {@code @Import} 聚合引入加密核心配置（{@link CryptoConfiguration}）、Hutool 加密实现配置 （{@link
 * HutoolCryptoConfiguration}）与 Web 加解密配置（{@link CryptoWebConfiguration}）。
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({CryptoConfiguration.class, HutoolCryptoConfiguration.class, CryptoWebConfiguration.class})
public class CryptoAutoConfiguration {
  /** 初始化完成后输出一条 trace 日志，用于确认自动配置已加载。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[CRYPTO] Crypto Auto Configuration");
  }
}
