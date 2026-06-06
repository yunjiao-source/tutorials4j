package tutorials4j.framework.feature.rest.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.crypto.core.AsymmetricCryptoStrategy;
import tutorials4j.framework.crypto.core.SymmetricCryptoStrategy;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;
import tutorials4j.framework.crypto.core.processor.CryptoProcessorFactory;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoEndpoint {
  private final AsymmetricCryptoStrategy asymmetricCryptoStrategy;
  private final SymmetricCryptoStrategy symmetricCryptoStrategy;

  @GetMapping("publicKey")
  public CryptoInfo getPublicKey() {
    CryptoProcessor cryptoProcessor =
        CryptoProcessorFactory.instance.findProcessor(asymmetricCryptoStrategy.getCategory());
    String publicKeyHex = cryptoProcessor.getSecretKey().publicKeyHex();
    return new CryptoInfo(asymmetricCryptoStrategy, symmetricCryptoStrategy, publicKeyHex);
  }
}
