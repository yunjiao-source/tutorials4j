package tutorials4j.toolkit.cache.caffeine.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link NamedCacheOptions} 单元测试：默认值与值对象语义。
 *
 * @author Yun Jiao
 */
class NamedCacheOptionsTest {

  @Test
  @DisplayName("默认值：写后 30 秒过期、最大 2000 条、不统计、初始容量与访问过期未设置")
  void shouldUseDefaultValues() {
    NamedCacheOptions options = new NamedCacheOptions();

    assertThat(options.getExpireAfterWrite()).isEqualTo(Duration.ofSeconds(30));
    assertThat(options.getExpireAfterAccess()).isNull();
    assertThat(options.isRecordStats()).isFalse();
    assertThat(options.getInitialCapacity()).isNull();
    assertThat(options.getMaximumSize()).isEqualTo(2000);
  }

  @Test
  @DisplayName("setter/getter 可读写全部配置项")
  void shouldAccessAllProperties() {
    NamedCacheOptions options = new NamedCacheOptions();

    options.setExpireAfterWrite(Duration.ofMinutes(5));
    options.setExpireAfterAccess(Duration.ofMinutes(1));
    options.setRecordStats(true);
    options.setInitialCapacity(64);
    options.setMaximumSize(500);

    assertThat(options.getExpireAfterWrite()).isEqualTo(Duration.ofMinutes(5));
    assertThat(options.getExpireAfterAccess()).isEqualTo(Duration.ofMinutes(1));
    assertThat(options.isRecordStats()).isTrue();
    assertThat(options.getInitialCapacity()).isEqualTo(64);
    assertThat(options.getMaximumSize()).isEqualTo(500);
  }

  @Test
  @DisplayName("值对象语义：配置相同即相等，配置不同则不相等")
  void shouldCompareByValue() {
    NamedCacheOptions first = new NamedCacheOptions();
    NamedCacheOptions second = new NamedCacheOptions();

    assertThat(first).isEqualTo(second).hasSameHashCodeAs(second);

    second.setMaximumSize(3000);
    assertThat(first).isNotEqualTo(second);

    second.setMaximumSize(first.getMaximumSize());
    second.setRecordStats(true);
    assertThat(first).isNotEqualTo(second);
  }
}
