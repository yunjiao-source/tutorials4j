package tutorials4j.framework.crypto.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.crypto.core.autoconfigure.CryptoConfiguration;
import tutorials4j.framework.crypto.hutool.autoconfigure.HutoolCryptoConfiguration;
import tutorials4j.framework.crypto.web.autoconfigure.CryptoWebConfiguration;

/**
 * 自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({CryptoConfiguration.class, HutoolCryptoConfiguration.class, CryptoWebConfiguration.class})
public class CryptoAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[CRYPTO] Crypto Auto Configuration");
  }
}
