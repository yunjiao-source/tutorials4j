package tutorials4j.framework.common.uid;

import cc.siyecao.uid.core.impl.DefaultUidGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.common.spring.core.SnowflakeIdProvider;
import tutorials4j.framework.common.spring.properties.UidProperties;

/**
 * 默认（实时计算）模式 UID 生成器实现，包装 {@link DefaultUidGenerator}。
 *
 * <p>每次调用 {@code getUID()} 实时生成唯一 ID，不进行缓存，适用于中等并发或对延迟不敏感的场景。 支持通过 {@link
 * DefaultUidGeneratorCustomizer} 进行配置定制。
 *
 * <p>内部使用固定纪元 "2026-05-21"，Worker ID 由 {@link SnowflakeIdProvider} 动态提供。
 *
 * @author Yun Jiao
 * @see DefaultUidGenerator
 * @see UidGenerator
 */
@Slf4j
@RequiredArgsConstructor
public class UidDefaultedGenerator implements UidGenerator {
  private DefaultUidGenerator generator;
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
  public void destroy() {}

  /**
   * 懒加载初始化底层 {@link DefaultUidGenerator}，应用所有定制器后调用 {@code afterPropertiesSet()}。
   *
   * @return 已就绪的生成器实例
   * @throws IllegalStateException 初始化失败
   */
  private DefaultUidGenerator getInstance() {
    if (generator != null) {
      return generator;
    }
    synchronized (UidDefaultedGenerator.class) {
      if (generator != null) {
        return generator;
      }
      DefaultUidGenerator gen = new DefaultUidGenerator();
      fill(properties, gen);
      customizers.forEach(customizer -> customizer.customize(gen));
      generator = gen;

      try {
        generator.afterPropertiesSet();
      } catch (Exception e) {
        throw new IllegalStateException("初始化DefaultUidGenerator异常", e);
      }
      return generator;
    }
  }
}
