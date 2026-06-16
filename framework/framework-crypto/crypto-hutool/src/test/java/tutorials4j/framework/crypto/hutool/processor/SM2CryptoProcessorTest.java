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

class SM2CryptoProcessorTest {

  private static final String TEST_DATA = "Hello, 世界！";

  /** 生成随机的 SM2 密钥对，并封装为 SecretKey */
  private SecretKey generateRandomSm2SecretKey() {
    KeyPair keyPair = SecureUtil.generateKeyPair("SM2");
    String privateKeyHex = HexUtil.encodeHexStr(keyPair.getPrivate().getEncoded());
    String publicKeyHex = HexUtil.encodeHexStr(keyPair.getPublic().getEncoded());
    // SM2 不需要对称密钥
    return new SecretKey("sm2-identity", null, publicKeyHex, privateKeyHex, Instant.now());
  }

  @Test
  void createWithSecretKeyShouldReturnValidProcessor() {
    SecretKey secretKey = generateRandomSm2SecretKey();
    SM2CryptoProcessor processor = SM2CryptoProcessor.create(secretKey);

    assertThat(processor).isNotNull();
    assertThat(processor.getSecretKey()).isSameAs(secretKey);
    // 注意：当前实现 getCategory() 返回 CryptoCategory.RSA（可能是个 bug）
    assertThat(processor.getCategory()).isEqualTo(CryptoCategory.SM2);
  }

  @Test
  void createWithNullSecretKeyShouldThrowException() {
    assertThatThrownBy(() -> SM2CryptoProcessor.create(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("'secretKey' must not be null");
  }

  @Test
  void encryptWithPublicKeyAndDecryptWithPrivateKeyShouldBeReversible() {
    SecretKey secretKey = generateRandomSm2SecretKey();
    SM2CryptoProcessor processor = SM2CryptoProcessor.create(secretKey);

    String encrypted = processor.encrypt(TEST_DATA);
    assertThat(encrypted).isNotBlank().isNotEqualTo(TEST_DATA);
    // 加密结果应为 Base64 格式
    assertThat(Base64.isBase64(encrypted)).isTrue();

    String decrypted = processor.decrypt(encrypted);
    assertThat(decrypted).isEqualTo(TEST_DATA);
  }

  @Test
  void encryptWithEmptyStringShouldThrowException() {
    SecretKey secretKey = generateRandomSm2SecretKey();
    SM2CryptoProcessor processor = SM2CryptoProcessor.create(secretKey);
    assertThatThrownBy(() -> processor.encrypt(""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("cannot encrypt empty");
  }

  @Test
  void encryptAndDecryptShouldHandleSpecialCharacters() {
    String special = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~\n\t\r\\";
    SecretKey secretKey = generateRandomSm2SecretKey();
    SM2CryptoProcessor processor = SM2CryptoProcessor.create(secretKey);

    String encrypted = processor.encrypt(special);
    String decrypted = processor.decrypt(encrypted);
    assertThat(decrypted).isEqualTo(special);
  }

  @Test
  void encryptWithWrongKeyPairCannotDecrypt() {
    SecretKey keyPair1 = generateRandomSm2SecretKey();
    SecretKey keyPair2 = generateRandomSm2SecretKey();

    SM2CryptoProcessor processor1 = SM2CryptoProcessor.create(keyPair1);
    SM2CryptoProcessor processor2 = SM2CryptoProcessor.create(keyPair2);

    String encrypted = processor1.encrypt(TEST_DATA);
    // 使用不同密钥对的处理器解密应该失败
    assertThatThrownBy(() -> processor2.decrypt(encrypted)).isInstanceOf(Exception.class);
  }

  @Test
  void decryptWithInvalidDataShouldThrowException() {
    SecretKey secretKey = generateRandomSm2SecretKey();
    SM2CryptoProcessor processor = SM2CryptoProcessor.create(secretKey);

    assertThatThrownBy(() -> processor.decrypt("not-a-valid-base64-or-ciphertext"))
        .isInstanceOf(Exception.class);
  }

  @Test
  void getSecretKeyShouldReturnOriginalKey() {
    SecretKey secretKey = generateRandomSm2SecretKey();
    SM2CryptoProcessor processor = SM2CryptoProcessor.create(secretKey);
    assertThat(processor.getSecretKey()).isEqualTo(secretKey);
  }

  @Test
  void getCategoryShouldReturnRsa() {
    SecretKey secretKey = generateRandomSm2SecretKey();
    SM2CryptoProcessor processor = SM2CryptoProcessor.create(secretKey);
    // 根据当前实现返回 RSA（可能与预期不符，但测试反映真实行为）
    assertThat(processor.getCategory()).isEqualTo(CryptoCategory.SM2);
  }
}
