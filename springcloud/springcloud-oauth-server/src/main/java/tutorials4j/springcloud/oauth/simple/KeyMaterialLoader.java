package tutorials4j.springcloud.oauth.simple;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * 密钥材料加载器：加载/生成 RSA 密钥对与密钥 ID，供 JWT 签发与校验使用。
 *
 * <p>当前实现于构造时一次性生成 RSA 密钥对；生产环境建议改为从 Kubernetes Secret、HashiCorp Vault 或云厂商 KMS/HSM 读取。
 *
 * @author Yun Jiao
 */
@Service
public class KeyMaterialLoader {

  // 缓存当前生成的密钥对，避免每次方法都重新生成
  private final RSAPublicKey publicKey;
  private final RSAPrivateKey privateKey;
  private final String kid;

  /** 构造加载器，并在初始化时生成 RSA 密钥对与唯一的密钥 ID。 */
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

  /** 返回 RSA 公钥。 */
  public RSAPublicKey loadPublicKey() {
    return this.publicKey;
  }

  /** 返回 RSA 私钥。 */
  public RSAPrivateKey loadPrivateKey() {
    return this.privateKey;
  }

  /** 返回当前密钥 ID。 */
  public String currentKid() {
    return this.kid;
  }
}
