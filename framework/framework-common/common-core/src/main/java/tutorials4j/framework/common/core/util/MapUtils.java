package tutorials4j.framework.common.core.util;

import java.util.Map;

/**
 * Map 工具类，提供不可变 Map 的创建与转换方法。
 *
 * @author Yun Jiao
 */
public class MapUtils {

  /**
   * 将传入的 Map 转换为不可变 Map；入参为 null 时返回空不可变 Map。
   *
   * @param m 原始 Map，可为 null
   * @return 不可变 Map
   */
  public static <K, V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> m) {
    return m != null ? Map.copyOf(m) : Map.of();
  }
}
