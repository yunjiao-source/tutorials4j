package tutorials4j.springcloud.oauth.simple;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * 对应的 KeyMaterialLoader 建议从以下来源读取：
 *
 * <p>• Kubernetes Secret 挂载文件 • HashiCorp Vault • 云厂商 KMS/HSM
 *
 * @author Yun Jiao
 */
@Service
public class KeyMaterialLoader {

  // 缓存当前生成的密钥对，避免每次方法都重新生成
  private final RSAPublicKey publicKey;
  private final RSAPrivateKey privateKey;
  private final String kid;

  public KeyMaterialLoader() {
    // 初始化时一次性生成 RSA 密钥对
    KeyPairGenerator keyGen = null;
    try {
      keyGen = KeyPairGenerator.getInstance("RSA");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    keyGen.initialize(2048); // 密钥长度 2048
    KeyPair keyPair = keyGen.generateKeyPair();

    this.publicKey = (RSAPublicKey) keyPair.getPublic();
    this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
    // 随机生成唯一 kid
    this.kid = UUID.randomUUID().toString();
  }

  public RSAPublicKey loadPublicKey() {
    return this.publicKey;
  }

  public RSAPrivateKey loadPrivateKey() {
    return this.privateKey;
  }

  public String currentKid() {
    return this.kid;
  }
}
