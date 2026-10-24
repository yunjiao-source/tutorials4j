package tutorials4j.toolkit.data.mybatisplus;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tutorials4j.toolkit.data.mybatisplus.support.SnowflakeIdentifierGenerator;

/**
 * {@link SnowflakeIdentifierGenerator} 单元测试：雪花算法 ID/UUID 生成。
 *
 * @author YunJiao Yang
 */
class SnowflakeIdentifierGeneratorTest {

  private final SnowflakeIdentifierGenerator generator = new SnowflakeIdentifierGenerator();

  @Test
  @DisplayName("实现 MyBatis-Plus 的 IdentifierGenerator")
  void shouldImplementIdentifierGenerator() {
    assertThat(generator).isInstanceOf(IdentifierGenerator.class);
  }

  @Test
  @DisplayName("nextId 生成 Long 类型雪花 ID")
  void shouldGenerateLongId() {
    Number id = generator.nextId(new Object());

    assertThat(id).isInstanceOf(Long.class);
    assertThat(id.longValue()).isPositive();
  }

  @Test
  @DisplayName("nextUUID 生成数字字符串")
  void shouldGenerateUuidString() {
    String uuid = generator.nextUUID(new Object());

    assertThat(uuid).matches("\\d+");
    assertThat(Long.parseLong(uuid)).isPositive();
  }

  @Test
  @DisplayName("多次调用生成唯一且递增的 ID")
  void shouldGenerateDistinctIncreasingIds() {
    Set<Long> ids = ConcurrentHashMap.newKeySet();
    long previous = Long.MIN_VALUE;
    for (int i = 0; i < 200; i++) {
      long id = generator.nextId(null).longValue();
      assertThat(id).isGreaterThan(previous);
      previous = id;
      ids.add(id);
    }

    assertThat(ids).hasSize(200);
  }

  @Test
  @DisplayName("多次调用生成互不相同的 UUID")
  void shouldGenerateDistinctUuids() {
    Set<String> uuids = ConcurrentHashMap.newKeySet();
    for (int i = 0; i < 200; i++) {
      uuids.add(generator.nextUUID(null));
    }

    assertThat(uuids).hasSize(200);
  }

  @Test
  @DisplayName("忽略实体参数，传 null 也能生成")
  void shouldIgnoreEntityArgument() {
    assertThat(generator.nextId(null)).isNotNull();
    assertThat(generator.nextUUID(null)).isNotNull();
  }

  @Test
  @DisplayName("nextUUID 与 nextId 使用同一套雪花算法，数值连续递增")
  void shouldShareSameSnowflakeSequence() {
    long fromUuid = Long.parseLong(generator.nextUUID(null));
    long fromId = generator.nextId(null).longValue();

    assertThat(fromId).isGreaterThan(fromUuid);
  }
}
