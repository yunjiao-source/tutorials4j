package tutorials4j.framework.common.uid;

import cc.siyecao.uid.core.impl.DefaultUidGenerator;

/**
 * 默认（实时计算）模式 UID 生成器定制函数接口。
 *
 * <p>允许用户在 Spring 配置中提供定制逻辑，对 {@link DefaultUidGenerator} 进行属性配置，例如时间回溯容忍度、Worker ID 分配器等。
 *
 * <pre>{@code
 * @Bean
 * public DefaultUidGeneratorCustomizer myCustomizer() {
 *     return generator -> {
 *         generator.setTimeBits(30);
 *         generator.setSeqBits(12);
 *     };
 * }
 * }</pre>
 *
 * @author Yun Jiao
 * @see UidDefaultedGenerator
 */
@FunctionalInterface
public interface DefaultUidGeneratorCustomizer {
  /**
   * 定制给定的 {@link DefaultUidGenerator} 实例。
   *
   * @param generator 待定制的生成器对象
   */
  void customize(DefaultUidGenerator generator);
}
