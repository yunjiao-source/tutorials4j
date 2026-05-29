package tutorials4j.framework.crypto.hutool.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.DigestCategory;

class HmacSHA256DigestProcessorTest {

  private static final String TEST_DATA = "Hello, 世界！";

  private SecretKey generateRandomHmacSha256Key() {
    // 生成随机的 HMAC 密钥（任意长度，通常建议 >= 32 字节）
    byte[] keyBytes = RandomUtil.randomBytes(32);
    String symmetricKeyHex = HexUtil.encodeHexStr(keyBytes);
    return new SecretKey("hmac-identity", symmetricKeyHex, null, null, Instant.now());
  }

  @Test
  void createWithSecretKeyShouldReturnValidProcessor() {
    SecretKey secretKey = generateRandomHmacSha256Key();
    HmacSHA256DigestProcessor processor = HmacSHA256DigestProcessor.create(secretKey);

    assertThat(processor).isNotNull();
    assertThat(processor.getSecretKey()).isSameAs(secretKey);
    assertThat(processor.getCategory()).isEqualTo(DigestCategory.HmacSHA256);
  }

  @Test
  void createWithNullSecretKeyShouldThrowException() {
    assertThatThrownBy(() -> HmacSHA256DigestProcessor.create(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("'secretKey' must not be empty or blank");
  }

  @Test
  void digestShouldBeDeterministicWithSameInputAndKey() {
    SecretKey secretKey = generateRandomHmacSha256Key();
    HmacSHA256DigestProcessor processor = HmacSHA256DigestProcessor.create(secretKey);

    String digest1 = processor.digest(TEST_DATA);
    String digest2 = processor.digest(TEST_DATA);

    assertThat(digest1).isNotBlank();
    assertThat(digest1).isEqualTo(digest2);
  }

  @Test
  void digestShouldChangeWhenDataChanges() {
    SecretKey secretKey = generateRandomHmacSha256Key();
    HmacSHA256DigestProcessor processor = HmacSHA256DigestProcessor.create(secretKey);

    String digest1 = processor.digest(TEST_DATA);
    String digest2 = processor.digest(TEST_DATA + "modified");

    assertThat(digest1).isNotEqualTo(digest2);
  }

  @Test
  void digestShouldChangeWhenKeyChanges() {
    SecretKey key1 = generateRandomHmacSha256Key();
    SecretKey key2 = generateRandomHmacSha256Key();

    HmacSHA256DigestProcessor processor1 = HmacSHA256DigestProcessor.create(key1);
    HmacSHA256DigestProcessor processor2 = HmacSHA256DigestProcessor.create(key2);

    String digest1 = processor1.digest(TEST_DATA);
    String digest2 = processor2.digest(TEST_DATA);

    assertThat(digest1).isNotEqualTo(digest2);
  }

  @Test
  void digestShouldHandleEmptyString() {
    SecretKey secretKey = generateRandomHmacSha256Key();
    HmacSHA256DigestProcessor processor = HmacSHA256DigestProcessor.create(secretKey);

    String digest = processor.digest("");
    assertThat(digest).isNotBlank();

    // 空字符串的摘要应该是确定性的
    assertThat(processor.digest("")).isEqualTo(digest);
  }

  @Test
  void digestShouldHandleSpecialCharacters() {
    String special = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~\n\t\r\\";
    SecretKey secretKey = generateRandomHmacSha256Key();
    HmacSHA256DigestProcessor processor = HmacSHA256DigestProcessor.create(secretKey);

    String digest = processor.digest(special);
    assertThat(digest).isNotBlank();
    assertThat(processor.digest(special)).isEqualTo(digest);
  }

  @Test
  void digestWithCharsetShouldUseGivenEncoding() {
    SecretKey secretKey = generateRandomHmacSha256Key();
    HmacSHA256DigestProcessor processor = HmacSHA256DigestProcessor.create(secretKey);

    String data = "中文测试";
    String digestUtf8 = processor.digest(data, StandardCharsets.UTF_8);
    String digestIso = processor.digest(data, StandardCharsets.ISO_8859_1);

    // 不同字符集下字节序列不同，摘要应不同
    assertThat(digestUtf8).isNotEqualTo(digestIso);
  }

  @Test
  void digestWithoutCharsetShouldDefaultToUtf8() {
    SecretKey secretKey = generateRandomHmacSha256Key();
    HmacSHA256DigestProcessor processor = HmacSHA256DigestProcessor.create(secretKey);

    String data = "Hello";
    String digestDefault = processor.digest(data);
    String digestUtf8 = processor.digest(data, StandardCharsets.UTF_8);

    assertThat(digestDefault).isEqualTo(digestUtf8);
  }

  @Test
  void getSecretKeyShouldReturnOriginalKey() {
    SecretKey secretKey = generateRandomHmacSha256Key();
    HmacSHA256DigestProcessor processor = HmacSHA256DigestProcessor.create(secretKey);

    assertThat(processor.getSecretKey()).isEqualTo(secretKey);
  }

  @Test
  void getCategoryShouldReturnHmacSha256() {
    SecretKey secretKey = generateRandomHmacSha256Key();
    HmacSHA256DigestProcessor processor = HmacSHA256DigestProcessor.create(secretKey);

    assertThat(processor.getCategory()).isEqualTo(DigestCategory.HmacSHA256);
  }
}
