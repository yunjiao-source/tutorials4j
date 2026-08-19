package tutorials4j.framework.cache.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * {@link CacheNamePrefix} 构建器及 {@code compute} 方法行为的单元测试。
 *
 * @author Yun Jiao
 */
public class CacheNamePrefixTest {
  /** 验证 tenant() 构建器叠加多个后缀并调用 end() 后，compute 结果带默认租户前缀与结尾分隔符。 */
  @Test
  void end() {
    CacheNamePrefix cacheNamePrefix =
        CacheNamePrefix.tenant().suffix("prefix1:").suffix("prefix2:").end();
    assertThat(cacheNamePrefix.compute("cacheName1"))
        .isEqualTo("DEFAULT:prefix1:prefix2:cacheName1::");
  }

  /** 验证 simple() 构建器叠加后缀后，compute 结果仅包含前缀与缓存名。 */
  @Test
  void simple() {
    CacheNamePrefix cacheNamePrefix = CacheNamePrefix.simple().suffix("prefix1:");
    assertThat(cacheNamePrefix.compute("cacheName1")).isEqualTo("prefix1:cacheName1");
  }

  /** 验证 prefix() 构建器叠加多个后缀后，compute 结果按顺序拼接前缀。 */
  @Test
  void prefix() {
    CacheNamePrefix cacheNamePrefix = CacheNamePrefix.prefix("prefix1:").suffix("prefix2:");
    assertThat(cacheNamePrefix.compute("cacheName1")).isEqualTo("prefix1:prefix2:cacheName1");
  }

  /** 验证 tenant() 构建器叠加多个后缀后，compute 结果带默认租户前缀。 */
  @Test
  void tenant() {
    CacheNamePrefix cacheNamePrefix =
        CacheNamePrefix.tenant().suffix("prefix1:").suffix("prefix2:");
    assertThat(cacheNamePrefix.compute("cacheName1"))
        .isEqualTo("DEFAULT:prefix1:prefix2:cacheName1");
  }
}
