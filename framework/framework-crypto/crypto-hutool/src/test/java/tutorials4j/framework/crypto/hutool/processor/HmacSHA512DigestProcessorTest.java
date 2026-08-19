package tutorials4j.framework.crypto.hutool.processor;

import static org.assertj.core.api.Assertions.assertThat;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.bean.DigestCategory;

/**
 * {@link HmacSHA512DigestProcessor} 单元测试
 *
 * @author Yun Jiao
 */
class HmacSHA512DigestProcessorTest {

  /** 测试数据：包含中文与特殊字符的明文字符串 */
  private static final String TEST_DATA = "Hello, 世界！";

  /** 生成随机的 HMAC-SHA512 密钥（64 字节），并封装为 {@link SecretKey} */
  private SecretKey generateRandomHmacSha512Key() {
    // 生成随机的 HMAC 密钥（通常建议 >= 64 字节，但任意长度均可）
    byte[] keyBytes = RandomUtil.randomBytes(64);
    String symmetricKeyHex = HexUtil.encodeHexStr(keyBytes);
    return new SecretKey("hmac-identity", symmetricKeyHex, null, null, Instant.now());
  }

  @Test
  void createWithSecretKeyShouldReturnValidProcessor() {
    SecretKey secretKey = generateRandomHmacSha512Key();
    HmacSHA512DigestProcessor processor = HmacSHA512DigestProcessor.create(secretKey);

    assertThat(processor).isNotNull();
    assertThat(processor.getSecretKey()).isSameAs(secretKey);
    assertThat(processor.getCategory()).isEqualTo(DigestCategory.HmacSHA512);
  }

  @Test
  void digestShouldBeDeterministicWithSameInputAndKey() {
    SecretKey secretKey = generateRandomHmacSha512Key();
    HmacSHA512DigestProcessor processor = HmacSHA512DigestProcessor.create(secretKey);

    String digest1 = processor.digest(TEST_DATA);
    String digest2 = processor.digest(TEST_DATA);

    assertThat(digest1).isNotBlank();
    assertThat(digest1).isEqualTo(digest2);
  }

  @Test
  void digestShouldChangeWhenDataChanges() {
    SecretKey secretKey = generateRandomHmacSha512Key();
    HmacSHA512DigestProcessor processor = HmacSHA512DigestProcessor.create(secretKey);

    String digest1 = processor.digest(TEST_DATA);
    String digest2 = processor.digest(TEST_DATA + "modified");

    assertThat(digest1).isNotEqualTo(digest2);
  }

  @Test
  void digestShouldChangeWhenKeyChanges() {
    SecretKey key1 = generateRandomHmacSha512Key();
    SecretKey key2 = generateRandomHmacSha512Key();

    HmacSHA512DigestProcessor processor1 = HmacSHA512DigestProcessor.create(key1);
    HmacSHA512DigestProcessor processor2 = HmacSHA512DigestProcessor.create(key2);

    String digest1 = processor1.digest(TEST_DATA);
    String digest2 = processor2.digest(TEST_DATA);

    assertThat(digest1).isNotEqualTo(digest2);
  }

  @Test
  void digestShouldHandleEmptyString() {
    SecretKey secretKey = generateRandomHmacSha512Key();
    HmacSHA512DigestProcessor processor = HmacSHA512DigestProcessor.create(secretKey);

    String digest = processor.digest("");
    assertThat(digest).isNotBlank();

    // 空字符串的摘要应该是确定性的
    assertThat(processor.digest("")).isEqualTo(digest);
  }

  @Test
  void digestShouldHandleSpecialCharacters() {
    String special = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~\n\t\r\\";
    SecretKey secretKey = generateRandomHmacSha512Key();
    HmacSHA512DigestProcessor processor = HmacSHA512DigestProcessor.create(secretKey);

    String digest = processor.digest(special);
    assertThat(digest).isNotBlank();
    assertThat(processor.digest(special)).isEqualTo(digest);
  }

  @Test
  void digestWithCharsetShouldUseGivenEncoding() {
    SecretKey secretKey = generateRandomHmacSha512Key();
    HmacSHA512DigestProcessor processor = HmacSHA512DigestProcessor.create(secretKey);

    String data = "中文测试";
    String digestUtf8 = processor.digest(data, StandardCharsets.UTF_8);
    String digestIso = processor.digest(data, StandardCharsets.ISO_8859_1);

    // 不同字符集下字节序列不同，摘要应不同
    assertThat(digestUtf8).isNotEqualTo(digestIso);
  }

  @Test
  void digestWithoutCharsetShouldDefaultToUtf8() {
    SecretKey secretKey = generateRandomHmacSha512Key();
    HmacSHA512DigestProcessor processor = HmacSHA512DigestProcessor.create(secretKey);

    String data = "Hello";
    String digestDefault = processor.digest(data);
    String digestUtf8 = processor.digest(data, StandardCharsets.UTF_8);

    assertThat(digestDefault).isEqualTo(digestUtf8);
  }

  @Test
  void getSecretKeyShouldReturnOriginalKey() {
    SecretKey secretKey = generateRandomHmacSha512Key();
    HmacSHA512DigestProcessor processor = HmacSHA512DigestProcessor.create(secretKey);

    assertThat(processor.getSecretKey()).isEqualTo(secretKey);
  }

  @Test
  void getCategoryShouldReturnHmacSha512() {
    SecretKey secretKey = generateRandomHmacSha512Key();
    HmacSHA512DigestProcessor processor = HmacSHA512DigestProcessor.create(secretKey);

    assertThat(processor.getCategory()).isEqualTo(DigestCategory.HmacSHA512);
  }
}
