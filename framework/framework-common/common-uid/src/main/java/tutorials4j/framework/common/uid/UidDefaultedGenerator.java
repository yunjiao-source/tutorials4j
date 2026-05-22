package tutorials4j.framework.common.uid;

import cc.siyecao.uid.core.impl.DefaultUidGenerator;
import java.util.List;
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
public class UidDefaultedGenerator extends AbstractUidGenerator {

  public UidDefaultedGenerator(
      UidProperties properties, List<DefaultUidGeneratorCustomizer> customizers) {
    super(properties, customizers);
  }

  @Override
  protected DefaultUidGenerator createUidGenerator() {
    return new DefaultUidGenerator();
  }

  @Override
  public void destroy() {}
}
