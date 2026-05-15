package tutorials4j.framework.cache.core.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.NonNull;

/**
 * 缓存工具
 *
 * @author Yun Jiao
 */
public class CacheUtils {
  /**
   * 生成基于类名、方法名和参数列表的缓存键生成器。
   *
   * <p>规则：{@code 目标类名:方法名:[参数1, 参数2, ...]}
   *
   * <ul>
   *   <li>类名：目标对象的 {@link Class#getSimpleName()}
   *   <li>方法名：当前执行的方法名称
   *   <li>参数列表：通过 {@link Arrays#toString(Object[])} 格式化
   * </ul>
   *
   * <p>此生成器可确保在同一类、同一方法、相同参数的情况下生成相同的缓存键， 适用于需要细粒度缓存控制的场景。
   *
   * @return 自定义的 {@link KeyGenerator} 实例
   */
  public static KeyGenerator classMethodParamsKeyGenerator() {
    return (Object target, Method method, @NonNull Object... params) -> {
      // 自定义 key 生成规则，例如：
      return target.getClass().getSimpleName()
          + ":"
          + method.getName()
          + ":"
          + Arrays.toString(params);
    };
  }
}
