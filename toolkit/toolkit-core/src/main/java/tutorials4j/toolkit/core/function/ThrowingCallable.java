package tutorials4j.toolkit.core.function;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface ThrowingCallable<V> {
  V call() throws Throwable;
}
