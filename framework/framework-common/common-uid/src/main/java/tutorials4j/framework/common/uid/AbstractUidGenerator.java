package tutorials4j.framework.common.uid;

import cc.siyecao.uid.core.impl.DefaultUidGenerator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.common.spring.properties.UidProperties;

/**
 * @author Yun Jiao
 */
@Slf4j
public abstract class AbstractUidGenerator implements UidGenerator {
  protected DefaultUidGenerator generator;
  protected final UidProperties properties;
  protected final List<DefaultUidGeneratorCustomizer> customizers;

  protected AbstractUidGenerator(
      UidProperties properties, List<DefaultUidGeneratorCustomizer> customizers) {
    this.properties = properties;
    this.customizers = customizers;
  }

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
    synchronized (AbstractUidGenerator.class) {
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
        throw new IllegalStateException("初始化异常:" + gen.getClass().getName(), e);
      }
      return generator;
    }
  }

  protected abstract DefaultUidGenerator createUidGenerator();
}
