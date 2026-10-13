package tutorials4j.toolkit.core.concurrent;

import java.util.concurrent.ThreadFactory;
import lombok.RequiredArgsConstructor;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@RequiredArgsConstructor
public class SimpleNamedThreadFactory implements ThreadFactory {
  private final String threadNamePrefix;
  private final boolean daemon;

  @Override
  public Thread newThread(Runnable r) {
    String uniquePrefix = threadNamePrefix + "-";
    Thread t = new Thread(r, uniquePrefix);
    t.setDaemon(daemon);
    return t;
  }
}
