package tutorials4j.framework.common.uid;

import cc.siyecao.uid.core.impl.CachedUidGenerator;
import cc.siyecao.uid.core.impl.DefaultUidGenerator;
import jakarta.annotation.PreDestroy;
import java.util.List;
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
public class UidCachedGenerator extends AbstractUidGenerator {

  public UidCachedGenerator(
      UidProperties properties, List<DefaultUidGeneratorCustomizer> customizers) {
    super(properties, customizers);
  }

  @Override
  @PreDestroy
  public void destroy() {
    if (generator == null) {
      return;
    }
    if (generator instanceof CachedUidGenerator cachedUidGenerator) {
      try {
        cachedUidGenerator.destroy();
      } catch (Exception e) {
        log.error("销毁CachedUidGenerator异常", e);
      }
    }

    if (log.isDebugEnabled()) {
      log.debug("[COMMON-UID] Uid Generator Cached 销毁成功");
    }
  }

  @Override
  protected DefaultUidGenerator createUidGenerator() {
    return new CachedUidGenerator();
  }
}
