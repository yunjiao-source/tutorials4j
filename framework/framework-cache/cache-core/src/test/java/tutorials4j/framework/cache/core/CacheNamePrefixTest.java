package tutorials4j.framework.cache.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class CacheNamePrefixTest {
  @Test
  void end() {
    CacheNamePrefix cacheNamePrefix =
        CacheNamePrefix.tenant().suffix("prefix1:").suffix("prefix2:").end();
    assertThat(cacheNamePrefix.compute("cacheName1"))
        .isEqualTo("DEFAULT:prefix1:prefix2:cacheName1::");
  }

  @Test
  void simple() {
    CacheNamePrefix cacheNamePrefix = CacheNamePrefix.simple().suffix("prefix1:");
    assertThat(cacheNamePrefix.compute("cacheName1")).isEqualTo("prefix1:cacheName1");
  }

  @Test
  void prefix() {
    CacheNamePrefix cacheNamePrefix = CacheNamePrefix.prefix("prefix1:").suffix("prefix2:");
    assertThat(cacheNamePrefix.compute("cacheName1")).isEqualTo("prefix1:prefix2:cacheName1");
  }

  @Test
  void tenant() {
    CacheNamePrefix cacheNamePrefix =
        CacheNamePrefix.tenant().suffix("prefix1:").suffix("prefix2:");
    assertThat(cacheNamePrefix.compute("cacheName1"))
        .isEqualTo("DEFAULT:prefix1:prefix2:cacheName1");
  }
}
