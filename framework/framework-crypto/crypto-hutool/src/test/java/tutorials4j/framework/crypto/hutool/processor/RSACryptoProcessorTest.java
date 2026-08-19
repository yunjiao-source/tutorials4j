package tutorials4j.framework.crypto.hutool.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import java.security.KeyPair;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.bean.CryptoCategory;

/**
 * {@link RSACryptoProcessor} 单元测试
 *
 * @author Yun Jiao
 */
class RSACryptoProcessorTest {

  /** 测试数据：包含中文与特殊字符的明文字符串 */
  private static final String TEST_DATA = "Hello, 世界！";

  /** 生成随机的 RSA 密钥对，并封装为 SecretKey */
  private SecretKey generateRandomRsaSecretKey() {
    KeyPair keyPair = SecureUtil.generateKeyPair("RSA", 2048);
    String privateKeyHex = HexUtil.encodeHexStr(keyPair.getPrivate().getEncoded());
    String publicKeyHex = HexUtil.encodeHexStr(keyPair.getPublic().getEncoded());
    // 对称密钥字段为 null
    return new SecretKey("rsa-identity", null, publicKeyHex, privateKeyHex, Instant.now());
  }

  @Test
  void createWithSecretKeyShouldReturnValidProcessor() {
    SecretKey secretKey = generateRandomRsaSecretKey();
    RSACryptoProcessor processor = RSACryptoProcessor.create(secretKey);

    assertThat(processor).isNotNull();
    assertThat(processor.getSecretKey()).isSameAs(secretKey);
    assertThat(processor.getCategory()).isEqualTo(CryptoCategory.RSA);
  }

  @Test
  void encryptWithPublicKeyAndDecryptWithPrivateKeyShouldBeReversible() {
    SecretKey secretKey = generateRandomRsaSecretKey();
    RSACryptoProcessor processor = RSACryptoProcessor.create(secretKey);

    String encrypted = processor.encrypt(TEST_DATA);
    assertThat(encrypted).isNotBlank().isNotEqualTo(TEST_DATA);
    // 加密结果应为 Base64 格式（encryptBase64 输出）
    assertThat(Base64.isBase64(encrypted)).isTrue();

    String decrypted = processor.decrypt(encrypted);
    assertThat(decrypted).isEqualTo(TEST_DATA);
  }

  @Test
  void encryptAndDecryptShouldHandleEmptyString() {
    SecretKey secretKey = generateRandomRsaSecretKey();
    RSACryptoProcessor processor = RSACryptoProcessor.create(secretKey);

    String encrypted = processor.encrypt("");
    assertThat(encrypted).isNotBlank();

    String decrypted = processor.decrypt(encrypted);
    assertThat(decrypted).isEmpty();
  }

  @Test
  void encryptAndDecryptShouldHandleSpecialCharacters() {
    String special = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~\n\t\r\\";
    SecretKey secretKey = generateRandomRsaSecretKey();
    RSACryptoProcessor processor = RSACryptoProcessor.create(secretKey);

    String encrypted = processor.encrypt(special);
    String decrypted = processor.decrypt(encrypted);
    assertThat(decrypted).isEqualTo(special);
  }

  @Test
  void encryptWithWrongKeyPairCannotDecrypt() {
    SecretKey keyPair1 = generateRandomRsaSecretKey();
    SecretKey keyPair2 = generateRandomRsaSecretKey();

    RSACryptoProcessor processor1 = RSACryptoProcessor.create(keyPair1);
    RSACryptoProcessor processor2 = RSACryptoProcessor.create(keyPair2);

    String encrypted = processor1.encrypt(TEST_DATA);
    // 使用不同密钥对的处理器解密应该失败
    assertThatThrownBy(() -> processor2.decrypt(encrypted)).isInstanceOf(Exception.class);
  }

  @Test
  void decryptWithInvalidDataShouldThrowException() {
    SecretKey secretKey = generateRandomRsaSecretKey();
    RSACryptoProcessor processor = RSACryptoProcessor.create(secretKey);

    assertThatThrownBy(() -> processor.decrypt("not-a-valid-base64-or-ciphertext"))
        .isInstanceOf(Exception.class);
  }

  @Test
  void getSecretKeyShouldReturnOriginalKey() {
    SecretKey secretKey = generateRandomRsaSecretKey();
    RSACryptoProcessor processor = RSACryptoProcessor.create(secretKey);
    assertThat(processor.getSecretKey()).isEqualTo(secretKey);
  }

  @Test
  void getCategoryShouldReturnRsa() {
    SecretKey secretKey = generateRandomRsaSecretKey();
    RSACryptoProcessor processor = RSACryptoProcessor.create(secretKey);
    assertThat(processor.getCategory()).isEqualTo(CryptoCategory.RSA);
  }
}
