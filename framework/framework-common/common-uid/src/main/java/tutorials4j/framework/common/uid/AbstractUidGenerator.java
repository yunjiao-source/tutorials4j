package tutorials4j.framework.common.uid;

import cc.siyecao.uid.core.impl.DefaultUidGenerator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.common.uid.autoconfigure.UidCommonProperties;

/**
 * UID 生成器抽象基类，封装底层 {@link DefaultUidGenerator} 的懒加载初始化与定制流程。
 *
 * <p>子类通过 {@link #createUidGenerator()} 提供具体的生成器实现（实时模式或缓存模式）， 首次调用时会应用所有 {@link
 * DefaultUidGeneratorCustomizer} 并完成初始化。
 *
 * @author Yun Jiao
 */
@Slf4j
public abstract class AbstractUidGenerator implements UidGenerator {

  /** 底层 UID 生成器实例，懒加载初始化 */
  protected DefaultUidGenerator generator;

  /** UID 生成器通用配置属性 */
  protected final UidCommonProperties properties;

  /** 用户提供的生成器定制器列表 */
  protected final List<DefaultUidGeneratorCustomizer> customizers;

  /**
   * 构造抽象生成器，保存配置属性与定制器列表。
   *
   * @param properties UID 生成器通用配置属性
   * @param customizers 用户提供的生成器定制器列表
   */
  protected AbstractUidGenerator(
      UidCommonProperties properties, List<DefaultUidGeneratorCustomizer> customizers) {
    this.properties = properties;
    this.customizers = customizers;
  }

  /**
   * 生成下一个唯一 ID。
   *
   * @return 下一个唯一 ID（长整型）
   */
  @Override
  public long nextUid() {
    return getInstance().getUID();
  }

  /**
   * 生成下一个唯一 ID 的字符串形式。
   *
   * @return 下一个唯一 ID 的字符串形式
   */
  @Override
  public String nextUidStr() {
    return String.valueOf(nextUid());
  }

  /**
   * 解析 UID 的组成信息。
   *
   * @param uid 待解析的 UID
   * @return 解析结果字符串
   */
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
      DefaultUidGenerator gen = createUidGenerator();
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

  /**
   * 创建具体的 UID 生成器实现。
   *
   * @return 具体的 UID 生成器实例
   */
  protected abstract DefaultUidGenerator createUidGenerator();
}
