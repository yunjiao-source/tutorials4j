package tutorials4j.framework.cache.core;

import java.util.Objects;
import org.springframework.util.Assert;
import tutorials4j.framework.common.core.SymbolConsts;
import tutorials4j.framework.common.core.TenantContextHolder;

/**
 * 缓存名前缀计算器。
 *
 * <p>定义缓存名称的前缀计算规则，提供租户前缀、简单前缀、固定前缀等工厂方法， 以及组合、追加后缀等默认组合方法，用于统一构造带前缀的缓存键。
 *
 * @author Yun Jiao
 */
public interface CacheNamePrefix {
  /** 缓存名与前缀之间的分隔符 */
  String SEPARATOR = SymbolConsts.COLON;

  /** 复合分隔符，用于标识前缀计算结束 */
  String DOUBLE_SEPARATOR = "::";

  /**
   * 计算给定缓存名的最终名称。
   *
   * @param cacheName 原始缓存名
   * @return 添加前缀后的缓存名
   */
  String compute(String cacheName);

  /** 返回在缓存名前添加当前租户 ID 的前缀计算器。 */
  static CacheNamePrefix tenant() {
    return name -> TenantContextHolder.get() + SEPARATOR + name;
  }

  /** 返回不添加任何前缀的简单前缀计算器。 */
  static CacheNamePrefix simple() {
    return name -> name;
  }

  /**
   * 返回在缓存名前添加固定前缀的前缀计算器。
   *
   * @param prefix 固定前缀
   * @return 固定前缀计算器
   */
  static CacheNamePrefix prefix(String prefix) {
    Assert.notNull(prefix, "prefix must not be null");
    return name -> prefix + name;
  }

  /** 返回在当前计算结果后追加双分隔符的前缀计算器。 */
  default CacheNamePrefix end() {
    return (name) -> this.compute(name) + DOUBLE_SEPARATOR;
  }

  /**
   * 返回在当前计算器之前追加固定后缀（作为前缀）的复合计算器。
   *
   * @param suffix 追加的前缀文本
   * @return 复合前缀计算器
   */
  default CacheNamePrefix suffix(String suffix) {
    return (name) -> prefix(suffix).andThen(this).compute(name);
  }

  /**
   * 返回先应用 {@code before} 再应用当前计算器的复合计算器。
   *
   * @param before 前置前缀计算器
   * @return 复合前缀计算器
   */
  default CacheNamePrefix compose(CacheNamePrefix before) {
    Objects.requireNonNull(before);
    return (name) -> compute(before.compute(name));
  }

  /**
   * 返回先应用当前计算器再应用 {@code after} 的复合计算器。
   *
   * @param after 后置前缀计算器
   * @return 复合前缀计算器
   */
  default CacheNamePrefix andThen(CacheNamePrefix after) {
    Objects.requireNonNull(after);
    return (name) -> after.compute(compute(name));
  }
}
