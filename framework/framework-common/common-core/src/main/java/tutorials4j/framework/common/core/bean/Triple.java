package tutorials4j.framework.common.core.bean;

/**
 * 三元组，用于封装三个相关联的值。
 *
 * @param <T1> 第一个值的类型
 * @param <T2> 第二个值的类型
 * @param <T3> 第三个值的类型
 * @author Yun Jiao
 */
public record Triple<T1, T2, T3>(T1 a, T2 b, T3 c) {
  /**
   * 创建包含三个值的三元组。
   *
   * @param a 第一个值
   * @param b 第二个值
   * @param c 第三个值
   * @return 新的三元组实例
   */
  public static <T1, T2, T3> Triple<T1, T2, T3> of(T1 a, T2 b, T3 c) {
    return new Triple<>(a, b, c);
  }
}
