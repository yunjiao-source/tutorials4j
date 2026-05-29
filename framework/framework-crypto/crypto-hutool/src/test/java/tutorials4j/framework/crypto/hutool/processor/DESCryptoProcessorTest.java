package tutorials4j.framework.crypto.hutool.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.CryptoCategory;

class DESCryptoProcessorTest {

  private static final String TEST_DATA = "Hello, 世界！";

  private SecretKey generateRandomDesSecretKey() {
    // DES 密钥长度为 8 字节（56 位有效）
    byte[] keyBytes = RandomUtil.randomBytes(8);
    String symmetricKeyHex = HexUtil.encodeHexStr(keyBytes);
    return new SecretKey("test-identity", symmetricKeyHex, null, null, Instant.now());
  }

  @Test
  void createWithSecretKeyShouldReturnValidProcessor() {
    SecretKey secretKey = generateRandomDesSecretKey();
    DESCryptoProcessor processor = DESCryptoProcessor.create(secretKey);

    assertThat(processor).isNotNull();
    assertThat(processor.getSecretKey()).isSameAs(secretKey);
    assertThat(processor.getCategory()).isEqualTo(CryptoCategory.DES);
  }

  @Test
  void createSecretKeyShouldThrowException() {
    DESCryptoProcessor processor = DESCryptoProcessor.create();

    String encrypted = processor.encrypt(TEST_DATA);
    assertThat(encrypted).isNotBlank().isNotEqualTo(TEST_DATA);

    String decrypted = processor.decrypt(encrypted);
    assertThat(decrypted).isEqualTo(TEST_DATA);
  }

  @Test
  void createWithNullSecretKeyShouldThrowException() {
    assertThatThrownBy(() -> DESCryptoProcessor.create(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("'secretKey' must not be empty or blank");
  }

  @Test
  void encryptAndDecryptShouldBeReversible() {
    SecretKey secretKey = generateRandomDesSecretKey();
    DESCryptoProcessor processor = DESCryptoProcessor.create(secretKey);

    // 按照正确语义：加密 -> 解密 -> 应得到原文
    String encrypted = processor.encrypt(TEST_DATA);
    assertThat(encrypted).isNotBlank().isNotEqualTo(TEST_DATA);

    String decrypted = processor.decrypt(encrypted);
    assertThat(decrypted).isEqualTo(TEST_DATA);
  }

  @Test
  void encryptAndDecryptShouldHandleEmptyString() {
    SecretKey secretKey = generateRandomDesSecretKey();
    DESCryptoProcessor processor = DESCryptoProcessor.create(secretKey);

    String encrypted = processor.encrypt("");
    assertThat(encrypted).isNotBlank();

    String decrypted = processor.decrypt(encrypted);
    assertThat(decrypted).isEmpty();
  }

  @Test
  void encryptAndDecryptShouldHandleSpecialCharacters() {
    String special = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~\n\t\r\\";
    SecretKey secretKey = generateRandomDesSecretKey();
    DESCryptoProcessor processor = DESCryptoProcessor.create(secretKey);

    String encrypted = processor.encrypt(special);
    String decrypted = processor.decrypt(encrypted);
    assertThat(decrypted).isEqualTo(special);
  }

  @Test
  void decryptWithInvalidDataShouldThrowException() {
    SecretKey secretKey = generateRandomDesSecretKey();
    DESCryptoProcessor processor = DESCryptoProcessor.create(secretKey);

    // 传入非法密文字符串，解密时应抛出异常（Hutool 会解码或解密失败）
    assertThatThrownBy(() -> processor.decrypt("invalid-hex-string")).isInstanceOf(Exception.class);
  }

  @Test
  void getSecretKeyShouldReturnOriginalKey() {
    SecretKey secretKey = generateRandomDesSecretKey();
    DESCryptoProcessor processor = DESCryptoProcessor.create(secretKey);

    assertThat(processor.getSecretKey()).isEqualTo(secretKey);
  }

  @Test
  void getCategoryShouldReturnDes() {
    SecretKey secretKey = generateRandomDesSecretKey();
    DESCryptoProcessor processor = DESCryptoProcessor.create(secretKey);

    assertThat(processor.getCategory()).isEqualTo(CryptoCategory.DES);
  }
}
