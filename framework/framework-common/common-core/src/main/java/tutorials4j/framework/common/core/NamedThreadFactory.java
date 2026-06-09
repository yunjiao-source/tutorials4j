package tutorials4j.framework.common.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class NamedThreadFactory implements ThreadFactory {
  private final AtomicInteger threadNumber = new AtomicInteger(1);
  private final String threadNamePrefix;
  private final boolean daemon;

  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(r, threadNamePrefix + threadNumber.getAndIncrement());
    t.setDaemon(daemon);
    return t;
  }
}
