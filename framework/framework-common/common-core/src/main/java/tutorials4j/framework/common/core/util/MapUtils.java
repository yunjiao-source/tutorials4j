package tutorials4j.framework.common.core.util;

import java.util.Map;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class MapUtils {
  public static <K, V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> m) {
    return m != null ? Map.copyOf(m) : Map.of();
  }
}
