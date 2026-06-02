package tutorials4j.framework.crypto.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;
import tutorials4j.framework.crypto.core.processor.CryptoProcessorFactory;
import tutorials4j.framework.crypto.core.properties.CryptoProperties;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoEndpoint {
  private final CryptoProperties properties;

  @GetMapping("publicKey")
  public String getPublicKey() {
    CryptoProcessor cryptoProcessor =
        CryptoProcessorFactory.instance.findProcessor(
            properties.getAsymmetricCryptoStrategy().getCategory());
    return cryptoProcessor.getSecretKey().publicKeyHex();
  }
}
