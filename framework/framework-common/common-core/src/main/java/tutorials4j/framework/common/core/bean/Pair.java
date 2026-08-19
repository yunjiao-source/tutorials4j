package tutorials4j.framework.common.core.bean;

/**
 * 二元组，用于封装两个相关联的值。
 *
 * @param <T1> 第一个值的类型
 * @param <T2> 第二个值的类型
 * @author Yun Jiao
 */
public record Pair<T1, T2>(T1 first, T2 second) {
  /**
   * 创建包含两个值的二元组。
   *
   * @param first 第一个值
   * @param second 第二个值
   * @return 新的二元组实例
   */
  public static <T1, T2> Pair<T1, T2> of(T1 first, T2 second) {
    return new Pair<>(first, second);
  }
}
