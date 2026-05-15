package tutorials4j.framework.common.core.support;

/**
 * 通用 Bean 创建器函数式接口。
 *
 * <p>定义了一个获取实例的核心方法，并提供了创建新实例及获取 Bean 类型的默认实现。 子类可以根据需要覆盖 {@link #newInstance()} 和 {@link
 * #getBeanClass()} 方法。
 *
 * @param <T> 创建的 Bean 类型
 * @author Yun Jiao
 */
@FunctionalInterface
public interface BeanCreator<T> {
  /**
   * 获取 Bean 实例（通常是从缓存中获取，若不存在则创建）。
   *
   * @return Bean 实例，不为 {@code null}
   */
  T getInstance();

  /**
   * 创建一个新的 Bean 实例，不经过缓存。
   *
   * <p>默认实现抛出 {@link UnsupportedOperationException}，子类应重写此方法以提供实际创建逻辑。
   *
   * @return 新创建的 Bean 实例
   * @throws UnsupportedOperationException 如果子类未实现该方法
   */
  default T newInstance() {
    throw new UnsupportedOperationException("newInstance");
  }

  /**
   * 获取 Bean 的 {@link Class} 类型。
   *
   * <p>默认实现抛出 {@link UnsupportedOperationException}，子类应重写此方法以提供实际类型信息。
   *
   * @return Bean 的 Class 对象
   * @throws UnsupportedOperationException 如果子类未实现该方法
   */
  default Class<T> getBeanClass() {
    throw new UnsupportedOperationException("getBeanClass");
  }
}
