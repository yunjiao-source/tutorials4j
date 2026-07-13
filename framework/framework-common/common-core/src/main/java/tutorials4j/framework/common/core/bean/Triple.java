package tutorials4j.framework.common.core.bean;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public record Triple<T1, T2, T3>(T1 a, T2 b, T3 c) {
  public static <T1, T2, T3> Triple<T1, T2, T3> of(T1 a, T2 b, T3 c) {
    return new Triple<>(a, b, c);
  }
}
