package tutorials4j.framework.crypto.web.endpoint;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.crypto.core.bean.AsymmetricCryptoStrategy;
import tutorials4j.framework.crypto.core.bean.SymmetricCryptoStrategy;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;
import tutorials4j.framework.crypto.core.processor.CryptoProcessorFactory;

/**
 * 加密功能端点。
 *
 * <p>提供与加密相关的对外接口，当前支持获取 RSA 公钥及当前使用的加解密策略信息， 供前端完成数据加密传输。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoEndpoint {
  private final AsymmetricCryptoStrategy asymmetricCryptoStrategy;
  private final SymmetricCryptoStrategy symmetricCryptoStrategy;

  /**
   * 获取加密公钥与加解密策略信息。
   *
   * @return 包含公钥十六进制串及非对称/对称加密策略的 {@link CryptoInfo}
   */
  @GetMapping("publicKey")
  public Result<CryptoInfo> getPublicKey() {
    CryptoProcessor cryptoProcessor =
        CryptoProcessorFactory.instance.findProcessor(asymmetricCryptoStrategy.getCategory());
    String publicKeyHex = cryptoProcessor.getSecretKey().publicKeyHex();
    CryptoInfo info =
        new CryptoInfo(asymmetricCryptoStrategy, symmetricCryptoStrategy, publicKeyHex);
    return Result.success(info);
  }
}
