package tutorials4j.framework.crypto.hutool.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.CryptoCategory;

class AESCryptoProcessorTest {

  private static final String TEST_DATA = "Hello, 世界！";

  private SecretKey generateRandomAesSecretKey() {
    // 生成随机的 AES-256 密钥（32 字节）
    byte[] keyBytes = RandomUtil.randomBytes(32);
    String symmetricKeyHex = HexUtil.encodeHexStr(keyBytes);
    return new SecretKey("test-identity", symmetricKeyHex, null, null, Instant.now());
  }

  @Test
  void createWithSecretKeyShouldReturnValidProcessor() {
    SecretKey secretKey = generateRandomAesSecretKey();
    AESCryptoProcessor processor = AESCryptoProcessor.create(secretKey);

    assertThat(processor).isNotNull();
    assertThat(processor.getSecretKey()).isSameAs(secretKey);
    assertThat(processor.getCategory()).isEqualTo(CryptoCategory.AES);
  }

  @Test
  void createSecretKeyShouldThrowException() {
    AESCryptoProcessor processor = AESCryptoProcessor.create();

    String encrypted = processor.encrypt(TEST_DATA);
    assertThat(encrypted).isNotBlank().isNotEqualTo(TEST_DATA);

    String decrypted = processor.decrypt(encrypted);
    assertThat(decrypted).isEqualTo(TEST_DATA);
  }

  @Test
  void createWithNullSecretKeyShouldThrowException() {
    assertThatThrownBy(() -> AESCryptoProcessor.create(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("'secretKey' must not be null");
  }

  @Test
  void encryptAndDecryptShouldBeReversible() {
    SecretKey secretKey = generateRandomAesSecretKey();
    AESCryptoProcessor processor = AESCryptoProcessor.create(secretKey);

    String encrypted = processor.encrypt(TEST_DATA);
    assertThat(encrypted).isNotBlank().isNotEqualTo(TEST_DATA);

    String decrypted = processor.decrypt(encrypted);
    assertThat(decrypted).isEqualTo(TEST_DATA);
  }

  @Test
  void encryptAndDecryptShouldHandleEmptyString() {
    SecretKey secretKey = generateRandomAesSecretKey();
    AESCryptoProcessor processor = AESCryptoProcessor.create(secretKey);

    String encrypted = processor.encrypt("");
    assertThat(encrypted).isNotBlank();

    String decrypted = processor.decrypt(encrypted);
    assertThat(decrypted).isEmpty();
  }

  @Test
  void encryptAndDecryptShouldHandleSpecialCharacters() {
    String special = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~\n\t\r\\";
    SecretKey secretKey = generateRandomAesSecretKey();
    AESCryptoProcessor processor = AESCryptoProcessor.create(secretKey);

    String encrypted = processor.encrypt(special);
    String decrypted = processor.decrypt(encrypted);
    assertThat(decrypted).isEqualTo(special);
  }

  @Test
  void decryptWithInvalidDataShouldThrowException() {
    SecretKey secretKey = generateRandomAesSecretKey();
    AESCryptoProcessor processor = AESCryptoProcessor.create(secretKey);

    assertThatThrownBy(() -> processor.decrypt("invalid-hex-string"))
        .isInstanceOf(Exception.class); // Hutool 会抛出解码或解密异常
  }

  @Test
  void getSecretKeyShouldReturnOriginalKey() {
    SecretKey secretKey = generateRandomAesSecretKey();
    AESCryptoProcessor processor = AESCryptoProcessor.create(secretKey);

    assertThat(processor.getSecretKey()).isEqualTo(secretKey);
  }

  @Test
  void getCategoryShouldReturnAes() {
    SecretKey secretKey = generateRandomAesSecretKey();
    AESCryptoProcessor processor = AESCryptoProcessor.create(secretKey);

    assertThat(processor.getCategory()).isEqualTo(CryptoCategory.AES);
  }
}
