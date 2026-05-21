package tutorials4j.framework.common.uid;

import cc.siyecao.uid.core.impl.CachedUidGenerator;
import jakarta.annotation.PreDestroy;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.common.spring.core.SnowflakeIdProvider;
import tutorials4j.framework.common.spring.properties.UidProperties;

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
@RequiredArgsConstructor
public class UidCachedGenerator implements UidGenerator {
  private CachedUidGenerator generator;
  private final UidProperties properties;
  private final List<DefaultUidGeneratorCustomizer> customizers;

  @Override
  public long nextUid() {
    return getInstance().getUID();
  }

  @Override
  public String nextUidStr() {
    return String.valueOf(nextUid());
  }

  @Override
  public String parseUid(long uid) {
    return getInstance().parseUID(uid);
  }

  @Override
  @PreDestroy
  public void destroy() {
    if (generator == null) {
      return;
    }
    try {
      generator.destroy();
    } catch (Exception e) {
      log.error("销毁CachedUidGenerator异常", e);
    }

    if (log.isDebugEnabled()) {
      log.debug("[COMMON-UID] Uid Generator Cached 销毁成功");
    }
  }

  /**
   * 懒加载初始化底层 {@link CachedUidGenerator}，应用所有定制器后调用 {@code afterPropertiesSet()}。
   *
   * @return 已就绪的生成器实例
   * @throws IllegalStateException 初始化失败
   */
  private CachedUidGenerator getInstance() {
    if (generator != null) {
      return generator;
    }
    synchronized (UidCachedGenerator.class) {
      if (generator != null) {
        return generator;
      }
      CachedUidGenerator gen = new CachedUidGenerator();
      fill(properties, gen);
      customizers.forEach(customizer -> customizer.customize(gen));
      generator = gen;

      try {
        generator.afterPropertiesSet();
      } catch (Exception e) {
        throw new IllegalStateException("初始化CachedUidGenerator异常", e);
      }
      return generator;
    }
  }
}
