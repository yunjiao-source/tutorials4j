package tutorials4j.framework.oss.fss.autoconfigure;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.oss.core.autoconfigure.OssProperties;
import tutorials4j.framework.oss.fss.metrics.FileStorageMetricsCollector;
import tutorials4j.framework.oss.fss.metrics.MetricsFileStorageAspect;

/**
 * 文件存储指标自动配置类。
 *
 * <p>当存在 {@link MeterRegistry} 且指标启用（默认启用）时，注册指标收集器和指标切面。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
@ConditionalOnClass(MeterRegistry.class)
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_OSS + "metrics",
    name = PropertiesConsts.PROPERTY_ENABLED,
    havingValue = "true",
    matchIfMissing = true)
public class FileStorageSpringMetricsConfiguration {

  @PostConstruct
  public void postConstruct() {
    log.trace("[OSS-SPRING-FILE-STORAGE] File Storage Spring Metrics Configuration");
  }

  /**
   * 创建指标收集器 Bean，负责记录操作成功/失败、大小、耗时等。
   *
   * @param registry Micrometer 注册表
   * @param properties OSS 配置（含 MetricsOptions）
   * @return 指标收集器实例
   */
  @Bean
  @ConditionalOnMissingBean
  FileStorageMetricsCollector fileStorageMetricsCollector(
      MeterRegistry registry, OssProperties properties) {
    log.trace("[OSS-SPRING-FILE-STORAGE] File Storage Metrics Collector");
    return new FileStorageMetricsCollector(registry, properties.getMetrics());
  }

  /**
   * 创建指标切面 Bean，实现文件操作拦截并调用收集器记录指标。 优先级设为最高，确保在日志切面前执行，避免影响耗时统计。
   *
   * @param collector 指标收集器
   * @param registry MeterRegistry
   * @return 指标切面实例
   */
  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  @ConditionalOnMissingBean
  MetricsFileStorageAspect metricsFileStorageAspect(
      FileStorageMetricsCollector collector, MeterRegistry registry) {
    log.trace("[OSS-SPRING-FILE-STORAGE] Metrics File Storage Aspect");
    return new MetricsFileStorageAspect(registry, collector);
  }
}
