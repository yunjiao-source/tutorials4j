package tutorials4j.framework.common.uid;

import cc.siyecao.uid.core.impl.CachedUidGenerator;
import cc.siyecao.uid.core.impl.DefaultUidGenerator;
import jakarta.annotation.PreDestroy;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.common.spring.core.SnowflakeIdProvider;
import tutorials4j.framework.common.uid.autoconfigure.UidCommonProperties;

/**
 * 缓存模式 UID 生成器实现，包装 {@link CachedUidGenerator}。
 *
 * <p>采用 RingBuffer 预先生成 UID，大幅提升高并发下的吞吐量。 支持通过 {@link DefaultUidGeneratorCustomizer} 进行灵活配置。
 *
 * <p>内部使用静态纪元时间 "2026-05-21"，Worker ID 通过 {@link SnowflakeIdProvider} 获取。 应用关闭时会自动销毁并释放资源。
 *
 * @author Yun Jiao
 * @see CachedUidGenerator
 * @see UidGenerator
 */
@Slf4j
public class UidCachedGenerator extends AbstractUidGenerator {

  /**
   * 构造缓存模式 UID 生成器。
   *
   * @param properties UID 生成器通用配置属性
   * @param customizers 用户提供的生成器定制器列表
   */
  public UidCachedGenerator(
      UidCommonProperties properties, List<DefaultUidGeneratorCustomizer> customizers) {
    super(properties, customizers);
  }

  /** 释放缓存生成器占用的资源，应用关闭时自动调用。 */
  @Override
  @PreDestroy
  public void destroy() {
    if (generator == null) {
      return;
    }

    if (log.isDebugEnabled()) {
      log.debug("CachedUidGenerator关闭");
    }

    if (generator instanceof CachedUidGenerator cachedUidGenerator) {
      try {
        cachedUidGenerator.destroy();
      } catch (Exception e) {
        log.error("关闭CachedUidGenerator异常", e);
      }
    }
  }

  /**
   * 创建缓存模式 UID 生成器实例。
   *
   * @return {@link CachedUidGenerator} 实例
   */
  @Override
  protected DefaultUidGenerator createUidGenerator() {
    return new CachedUidGenerator();
  }
}
