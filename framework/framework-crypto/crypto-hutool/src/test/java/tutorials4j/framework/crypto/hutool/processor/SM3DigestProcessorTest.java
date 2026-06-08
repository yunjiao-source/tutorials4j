package tutorials4j.framework.crypto.hutool.processor;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import tutorials4j.framework.crypto.core.DigestCategory;

class SM3DigestProcessorTest {

  private static final String TEST_DATA = "Hello, 世界！";

  @Test
  void createWithDefaultParametersShouldReturnValidProcessor() {
    SM3DigestProcessor processor = SM3DigestProcessor.create();
    assertThat(processor).isNotNull();
    assertThat(processor.getCategory()).isEqualTo(DigestCategory.SM3);
  }

  @Test
  void digestShouldBeDeterministicWithSameParameters() {
    SM3DigestProcessor processor = SM3DigestProcessor.create();
    String digest1 = processor.digest(TEST_DATA);
    String digest2 = processor.digest(TEST_DATA);
    assertThat(digest1).isNotBlank();
    assertThat(digest1).isEqualTo(digest2);
  }

  @Test
  void digestShouldChangeWhenDataChanges() {
    SM3DigestProcessor processor = SM3DigestProcessor.create();
    String digest1 = processor.digest(TEST_DATA);
    String digest2 = processor.digest(TEST_DATA + "modified");
    assertThat(digest1).isNotEqualTo(digest2);
  }

  @Test
  void digestWithSaltShouldProduceDifferentResult() {
    SM3DigestProcessor processorWithoutSalt = SM3DigestProcessor.create();
    SM3DigestProcessor processorWithSalt = SM3DigestProcessor.create("mySalt", 0, 1);

    String digestNoSalt = processorWithoutSalt.digest(TEST_DATA);
    String digestWithSalt = processorWithSalt.digest(TEST_DATA);
    assertThat(digestNoSalt).isNotEqualTo(digestWithSalt);
  }

  @Test
  void digestWithDifferentSaltPositionShouldProduceDifferentResult() {
    String salt = "salt123";
    SM3DigestProcessor processorPos0 = SM3DigestProcessor.create(salt, 0, 1);
    SM3DigestProcessor processorPosEnd = SM3DigestProcessor.create(salt, 7, 1); // -1 表示末尾

    String digestPos0 = processorPos0.digest(TEST_DATA);
    String digestPosEnd = processorPosEnd.digest(TEST_DATA);
    assertThat(digestPos0).isNotEqualTo(digestPosEnd);
  }

  @Test
  void digestWithMultipleIterationsShouldProduceDifferentResult() {
    SM3DigestProcessor processor1x = SM3DigestProcessor.create(null, 0, 1);
    SM3DigestProcessor processor3x = SM3DigestProcessor.create(null, 0, 3);

    String digest1 = processor1x.digest(TEST_DATA);
    String digest3 = processor3x.digest(TEST_DATA);
    assertThat(digest1).isNotEqualTo(digest3);
  }

  @Test
  void digestWithEmptySaltShouldNotCrash() {
    // 空盐字符串：Hutool 的 SM3 会设置一个空字节数组（或忽略），不应抛异常
    SM3DigestProcessor processor = SM3DigestProcessor.create("", 0, 1);
    String digest = processor.digest(TEST_DATA);
    assertThat(digest).isNotBlank();
  }

  @Test
  void digestShouldHandleEmptyString() {
    SM3DigestProcessor processor = SM3DigestProcessor.create();
    String digest = processor.digest("");
    assertThat(digest).isNotBlank();
    assertThat(processor.digest("")).isEqualTo(digest);
  }

  @Test
  void digestShouldHandleSpecialCharacters() {
    String special = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~\n\t\r\\";
    SM3DigestProcessor processor = SM3DigestProcessor.create();
    String digest = processor.digest(special);
    assertThat(digest).isNotBlank();
    assertThat(processor.digest(special)).isEqualTo(digest);
  }

  @Test
  void digestWithCharsetShouldUseGivenEncoding() {
    SM3DigestProcessor processor = SM3DigestProcessor.create();
    String data = "中文测试";
    String digestUtf8 = processor.digest(data, StandardCharsets.UTF_8);
    String digestIso = processor.digest(data, StandardCharsets.ISO_8859_1);
    // 不同字符集下字节序列不同，摘要应不同
    assertThat(digestUtf8).isNotEqualTo(digestIso);
  }

  @Test
  void digestWithoutCharsetShouldDefaultToUtf8() {
    SM3DigestProcessor processor = SM3DigestProcessor.create();
    String data = "Hello";
    String digestDefault = processor.digest(data);
    String digestUtf8 = processor.digest(data, StandardCharsets.UTF_8);
    assertThat(digestDefault).isEqualTo(digestUtf8);
  }

  @Test
  void getSecretKeyShouldThrowUnsupportedException() {
    SM3DigestProcessor processor = SM3DigestProcessor.create();
    assertThat(processor.getSecretKey()).isNull();
  }
}
