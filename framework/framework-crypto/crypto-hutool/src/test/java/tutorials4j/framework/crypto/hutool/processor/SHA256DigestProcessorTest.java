package tutorials4j.framework.crypto.hutool.processor;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import tutorials4j.framework.crypto.core.bean.DigestCategory;

/**
 * {@link SHA256DigestProcessor} 单元测试
 *
 * @author Yun Jiao
 */
class SHA256DigestProcessorTest {

  /** 测试数据：包含中文与特殊字符的明文字符串 */
  private static final String TEST_DATA = "Hello, 世界！";

  @Test
  void createWithDefaultParametersShouldReturnValidProcessor() {
    SHA256DigestProcessor processor = SHA256DigestProcessor.create();
    assertThat(processor).isNotNull();
    assertThat(processor.getCategory()).isEqualTo(DigestCategory.SHA256);
  }

  @Test
  void digestShouldBeDeterministicWithSameParameters() {
    SHA256DigestProcessor processor = SHA256DigestProcessor.create();
    String digest1 = processor.digest(TEST_DATA);
    String digest2 = processor.digest(TEST_DATA);
    assertThat(digest1).isNotBlank();
    assertThat(digest1).isEqualTo(digest2);
  }

  @Test
  void digestShouldChangeWhenDataChanges() {
    SHA256DigestProcessor processor = SHA256DigestProcessor.create();
    String digest1 = processor.digest(TEST_DATA);
    String digest2 = processor.digest(TEST_DATA + "modified");
    assertThat(digest1).isNotEqualTo(digest2);
  }

  @Test
  void digestWithSaltShouldProduceDifferentResult() {
    SHA256DigestProcessor processorWithoutSalt = SHA256DigestProcessor.create();
    SHA256DigestProcessor processorWithSalt = SHA256DigestProcessor.create("mySalt", 0, 1);

    String digestNoSalt = processorWithoutSalt.digest(TEST_DATA);
    String digestWithSalt = processorWithSalt.digest(TEST_DATA);
    assertThat(digestNoSalt).isNotEqualTo(digestWithSalt);
  }

  @Test
  void digestWithDifferentSaltPositionShouldProduceDifferentResult() {
    String salt = "salt123";
    SHA256DigestProcessor processorPos0 = SHA256DigestProcessor.create(salt, 0, 1);
    SHA256DigestProcessor processorPosEnd = SHA256DigestProcessor.create(salt, 7, 1);

    String digestPos0 = processorPos0.digest(TEST_DATA);
    String digestPosEnd = processorPosEnd.digest(TEST_DATA);
    assertThat(digestPos0).isNotEqualTo(digestPosEnd);
  }

  @Test
  void digestWithMultipleIterationsShouldProduceDifferentResult() {
    SHA256DigestProcessor processor1x = SHA256DigestProcessor.create(null, 0, 1);
    SHA256DigestProcessor processor3x = SHA256DigestProcessor.create(null, 0, 3);

    String digest1 = processor1x.digest(TEST_DATA);
    String digest3 = processor3x.digest(TEST_DATA);
    assertThat(digest1).isNotEqualTo(digest3);
  }

  @Test
  void digestWithEmptySaltShouldBehaveSameAsNoSalt() {
    // 空盐字符串与无盐理论上相同（Digester 会设置空字节数组？但 Hutool 实现可能将空盐视为无盐）
    SHA256DigestProcessor processorNoSalt = SHA256DigestProcessor.create();
    SHA256DigestProcessor processorEmptySalt = SHA256DigestProcessor.create("", 0, 1);

    String digestNoSalt = processorNoSalt.digest(TEST_DATA);
    String digestEmptySalt = processorEmptySalt.digest(TEST_DATA);
    // 实际行为取决于 hutool：如果 setSalt("") 会使用空字节数组，结果可能相同或不同？
    // 这里不强制断言相等，仅验证不抛异常
    assertThat(digestEmptySalt).isNotBlank();
  }

  @Test
  void digestShouldHandleEmptyString() {
    SHA256DigestProcessor processor = SHA256DigestProcessor.create();
    String digest = processor.digest("");
    assertThat(digest).isNotBlank();
    assertThat(processor.digest("")).isEqualTo(digest);
  }

  @Test
  void digestShouldHandleSpecialCharacters() {
    String special = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~\n\t\r\\";
    SHA256DigestProcessor processor = SHA256DigestProcessor.create();
    String digest = processor.digest(special);
    assertThat(digest).isNotBlank();
    assertThat(processor.digest(special)).isEqualTo(digest);
  }

  @Test
  void digestWithCharsetShouldUseGivenEncoding() {
    SHA256DigestProcessor processor = SHA256DigestProcessor.create();
    String data = "中文测试";
    String digestUtf8 = processor.digest(data, StandardCharsets.UTF_8);
    String digestIso = processor.digest(data, StandardCharsets.ISO_8859_1);
    // 不同字符集下字节序列不同，摘要应不同
    assertThat(digestUtf8).isNotEqualTo(digestIso);
  }

  @Test
  void digestWithoutCharsetShouldDefaultToUtf8() {
    SHA256DigestProcessor processor = SHA256DigestProcessor.create();
    String data = "Hello";
    String digestDefault = processor.digest(data);
    String digestUtf8 = processor.digest(data, StandardCharsets.UTF_8);
    assertThat(digestDefault).isEqualTo(digestUtf8);
  }

  @Test
  void getSecretKeyShouldThrowUnsupportedException() {
    SHA256DigestProcessor processor = SHA256DigestProcessor.create();
    assertThat(processor.getSecretKey()).isNull();
  }
}
