package tutorials4j.framework.common.core.support;

/**
 * 允许抛出受检异常的 {@link java.util.concurrent.Callable} 变体。
 *
 * <p>用于在 Lambda 表达式中调用会抛出受检异常的方法，由调用方自行决定如何处理异常。
 *
 * @param <V> 返回值类型
 * @author Yun Jiao
 */
@FunctionalInterface
public interface ThrowingCallable<V> {

  /**
   * 执行任务并返回结果，允许抛出任意异常。
   *
   * @return 任务执行结果
   * @throws Throwable 执行过程中可能抛出的任意异常
   */
  V call() throws Throwable;
}
