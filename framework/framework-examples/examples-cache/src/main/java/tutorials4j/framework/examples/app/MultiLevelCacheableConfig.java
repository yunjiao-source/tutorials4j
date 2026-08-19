package tutorials4j.framework.examples.app;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tutorials4j.framework.cache.multi.MultiLevelCacheManagerCreator;

/**
 * 多级（两级）缓存示例配置，仅在 {@code multi-level} Profile 下生效。
 *
 * <p>启用缓存并注册多级缓存管理器，同时扫描多级缓存示例包 {@code tutorials4j.framework.examples.multi} 中的组件。
 *
 * @author Yun Jiao
 */
@EnableCaching
@Configuration
@Profile("multi-level")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.multi"})
public class MultiLevelCacheableConfig {
  /**
   * 注册多级缓存管理器 Bean。
   *
   * @param cacheManagerCreator 多级缓存管理器创建器
   * @return 多级缓存管理器实例
   */
  @Bean
  public CacheManager cacheManager(MultiLevelCacheManagerCreator cacheManagerCreator) {
    return cacheManagerCreator.getInstance();
  }
}
