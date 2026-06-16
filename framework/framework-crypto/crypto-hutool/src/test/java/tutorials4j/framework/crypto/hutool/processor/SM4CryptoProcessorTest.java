package tutorials4j.framework.crypto.hutool.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.bean.CryptoCategory;

class SM4CryptoProcessorTest {

  private static final String TEST_DATA = "Hello, 世界！";

  private SecretKey generateRandomSm4SecretKey() {
    // SM4 密钥长度为 16 字节（128 位）
    byte[] keyBytes = RandomUtil.randomBytes(16);
    String symmetricKeyHex = HexUtil.encodeHexStr(keyBytes);
    return new SecretKey("sm4-identity", symmetricKeyHex, null, null, Instant.now());
  }

  @Test
  void createWithSecretKeyShouldReturnValidProcessor() {
    SecretKey secretKey = generateRandomSm4SecretKey();
    SM4CryptoProcessor processor = SM4CryptoProcessor.create(secretKey);

    assertThat(processor).isNotNull();
    assertThat(processor.getSecretKey()).isSameAs(secretKey);
    assertThat(processor.getCategory()).isEqualTo(CryptoCategory.SM4);
  }

  @Test
  void createWithNullSecretKeyShouldThrowException() {
    assertThatThrownBy(() -> SM4CryptoProcessor.create(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("'secretKey' must not be null");
  }

  @Test
  void encryptAndDecryptShouldBeReversible() {
    SecretKey secretKey = generateRandomSm4SecretKey();
    SM4CryptoProcessor processor = SM4CryptoProcessor.create(secretKey);

    // 按照正确语义：加密 -> 解密 -> 应得到原文
    String encrypted = processor.encrypt(TEST_DATA);
    assertThat(encrypted).isNotBlank().isNotEqualTo(TEST_DATA);

    String decrypted = processor.decrypt(encrypted);
    assertThat(decrypted).isEqualTo(TEST_DATA);
  }

  @Test
  void encryptAndDecryptShouldHandleEmptyString() {
    SecretKey secretKey = generateRandomSm4SecretKey();
    SM4CryptoProcessor processor = SM4CryptoProcessor.create(secretKey);

    String encrypted = processor.encrypt("");
    assertThat(encrypted).isNotBlank();

    String decrypted = processor.decrypt(encrypted);
    assertThat(decrypted).isEmpty();
  }

  @Test
  void encryptAndDecryptShouldHandleSpecialCharacters() {
    String special = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~\n\t\r\\";
    SecretKey secretKey = generateRandomSm4SecretKey();
    SM4CryptoProcessor processor = SM4CryptoProcessor.create(secretKey);

    String encrypted = processor.encrypt(special);
    String decrypted = processor.decrypt(encrypted);
    assertThat(decrypted).isEqualTo(special);
  }

  @Test
  void encryptWithDifferentKeyCannotDecrypt() {
    SecretKey key1 = generateRandomSm4SecretKey();
    SecretKey key2 = generateRandomSm4SecretKey();

    SM4CryptoProcessor processor1 = SM4CryptoProcessor.create(key1);
    SM4CryptoProcessor processor2 = SM4CryptoProcessor.create(key2);

    String encrypted = processor1.encrypt(TEST_DATA);
    // 使用不同密钥解密应该失败或得到乱码（SM4 会抛出异常或返回错误数据）
    assertThatThrownBy(() -> processor2.decrypt(encrypted)).isInstanceOf(Exception.class);
  }

  @Test
  void decryptWithInvalidDataShouldThrowException() {
    SecretKey secretKey = generateRandomSm4SecretKey();
    SM4CryptoProcessor processor = SM4CryptoProcessor.create(secretKey);

    assertThatThrownBy(() -> processor.decrypt("invalid-hex-string")).isInstanceOf(Exception.class);
  }

  @Test
  void getSecretKeyShouldReturnOriginalKey() {
    SecretKey secretKey = generateRandomSm4SecretKey();
    SM4CryptoProcessor processor = SM4CryptoProcessor.create(secretKey);
    assertThat(processor.getSecretKey()).isEqualTo(secretKey);
  }

  @Test
  void getCategoryShouldReturnSm4() {
    SecretKey secretKey = generateRandomSm4SecretKey();
    SM4CryptoProcessor processor = SM4CryptoProcessor.create(secretKey);
    assertThat(processor.getCategory()).isEqualTo(CryptoCategory.SM4);
  }
}
