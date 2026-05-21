package tutorials4j.framework.common.uid;

import cc.siyecao.uid.core.impl.CachedUidGenerator;
import cc.siyecao.uid.core.impl.DefaultUidGenerator;
import cn.hutool.extra.spring.SpringUtil;

/**
 * 框架 UID 生成器静态工具类。
 *
 * <p>提供两种预置的生成器实例：
 *
 * <ul>
 *   <li>{@link #CACHED} —— 缓存模式（高并发推荐）
 *   <li>{@link #DEFAULTED} —— 默认实时模式
 * </ul>
 *
 * 内部通过 {@link SpringUtil} 获取 Spring 容器中对应的 Bean，因此需保证 Spring 环境已初始化。
 *
 * <pre>{@code
 * long id = UidUtils.DEFAULTED.nextUid();
 * String idStr = UidUtils.CACHED.nextUidStr();
 * }</pre>
 *
 * @author Yun Jiao (重构)
 */
public final class UidUtils {

  private UidUtils() {}

  /**
   * 缓存模式 UID 生成器（推荐高并发场景）。
   *
   * <p>内部使用 {@link CachedUidGenerator}，具备 RingBuffer 预取能力。
   */
  public static final UidGenerator CACHED = CachedHolder.INSTANCE;

  /**
   * 简单模式 UID 生成器。
   *
   * <p>内部使用 {@link DefaultUidGenerator}，每次实时计算。
   */
  public static final UidGenerator DEFAULTED = DefaultedHolder.INSTANCE;

  // ------------------------------------------------------------------
  // 缓存模式实现（基于 CachedUidGenerator）
  // ------------------------------------------------------------------
  private static final class CachedHolder {
    static final UidGenerator INSTANCE = SpringUtil.getBean(UidCachedGenerator.class);
  }

  // ------------------------------------------------------------------
  // 默认模式实现（基于 DefaultUidGenerator）
  // ------------------------------------------------------------------
  private static final class DefaultedHolder {
    static final UidGenerator INSTANCE = SpringUtil.getBean(UidDefaultedGenerator.class);
  }
}
