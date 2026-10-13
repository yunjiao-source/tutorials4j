package tutorials4j.toolkit.cache.caffeine.component;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tutorials4j.toolkit.cache.CacheType;
import tutorials4j.toolkit.cache.caffeine.autoconfigure.CaffeineCacheToolkitProperties;

/**
 * {@link CaffeineCacheManagerCreator} 单元测试：类型声明与实例创建策略。
 *
 * @author YunJiao Yang
 */
class CaffeineCacheManagerCreatorTest {

  private static CaffeineCacheToolkitProperties properties() {
    return new CaffeineCacheToolkitProperties();
  }

  @Test
  @DisplayName("缓存类型声明为 caffeine")
  void shouldReturnCaffeineCacheType() {
    CaffeineCacheManagerCreator creator = new CaffeineCacheManagerCreator(properties());

    assertThat(creator.getCacheType()).isEqualTo(CacheType.caffeine);
  }

  @Test
  @DisplayName("Bean 类型声明为 CaffeineNamedCacheManager")
  void shouldReturnBeanClass() {
    CaffeineCacheManagerCreator creator = new CaffeineCacheManagerCreator(properties());

    assertThat(creator.getBeanClass()).isEqualTo(CaffeineNamedCacheManager.class);
  }

  @Test
  @DisplayName("getInstance 首次创建后复用同一实例")
  void shouldReuseInstance() {
    CaffeineCacheManagerCreator creator = new CaffeineCacheManagerCreator(properties());

    CaffeineNamedCacheManager first = creator.getInstance();

    assertThat(first).isNotNull();
    assertThat(creator.getInstance()).isSameAs(first);
  }

  @Test
  @DisplayName("newInstance 每次返回全新实例")
  void shouldCreateNewInstanceEachTime() {
    CaffeineCacheManagerCreator creator = new CaffeineCacheManagerCreator(properties());

    CaffeineNamedCacheManager first = creator.newInstance();

    assertThat(first).isNotNull();
    assertThat(creator.newInstance()).isNotSameAs(first);
  }

  @Test
  @DisplayName("创建出的实例继承配置：cacheNullValues=false 时不允许缓存空值")
  void shouldApplyProperties() {
    CaffeineCacheToolkitProperties properties = properties();
    properties.setCacheNullValues(false);

    CaffeineCacheManagerCreator creator = new CaffeineCacheManagerCreator(properties);

    assertThat(creator.getInstance().isAllowNullValues()).isFalse();
    assertThat(creator.newInstance().isAllowNullValues()).isFalse();
  }

  @Test
  @DisplayName("getInstance 与 newInstance 返回不同实例，且 getInstance 不缓存 newInstance 的结果")
  void shouldKeepSingletonSeparateFromNewInstance() {
    CaffeineCacheManagerCreator creator = new CaffeineCacheManagerCreator(properties());

    CaffeineNamedCacheManager singleton = creator.getInstance();

    assertThat(creator.newInstance()).isNotSameAs(singleton);
    assertThat(creator.getInstance()).isSameAs(singleton);
  }

  @Test
  @DisplayName("并发调用 getInstance 只创建一个实例")
  void shouldCreateSingleInstanceConcurrently() throws Exception {
    CaffeineCacheManagerCreator creator = new CaffeineCacheManagerCreator(properties());
    int threads = 16;

    try (var executor = Executors.newFixedThreadPool(threads)) {
      List<Future<CaffeineNamedCacheManager>> futures = new ArrayList<>();
      for (int i = 0; i < threads; i++) {
        futures.add(executor.submit(creator::getInstance));
      }

      List<CaffeineNamedCacheManager> instances = new ArrayList<>();
      for (Future<CaffeineNamedCacheManager> future : futures) {
        instances.add(future.get());
      }

      assertThat(instances).allMatch(instance -> instance == instances.get(0));
    }
  }
}
