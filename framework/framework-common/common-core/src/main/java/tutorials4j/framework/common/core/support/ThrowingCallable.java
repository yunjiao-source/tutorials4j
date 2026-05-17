package tutorials4j.framework.common.core.support;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface ThrowingCallable<V> {
  V call() throws Throwable;
}
