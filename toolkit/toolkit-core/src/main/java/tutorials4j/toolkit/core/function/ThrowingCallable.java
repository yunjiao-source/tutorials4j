package tutorials4j.toolkit.core.function;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@FunctionalInterface
public interface ThrowingCallable<V> {
  V call() throws Throwable;
}
