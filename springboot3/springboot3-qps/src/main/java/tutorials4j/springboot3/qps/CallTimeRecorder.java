package tutorials4j.springboot3.qps;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * 用于记录最近N次调用时间
 *
 * @author Yun Jiao
 */
public class CallTimeRecorder {
  // 存储最近100次调用时间的数据结构
  private static final int RECENT_CALLS_SIZE = 100;

  private final AtomicReferenceArray<Long> recentCallTimes;
  private final AtomicInteger index = new AtomicInteger(0);
  private final AtomicInteger count = new AtomicInteger(0);

  public CallTimeRecorder() {
    this.recentCallTimes = new AtomicReferenceArray<>(RECENT_CALLS_SIZE);
  }

  public void recordCallTime(long durationMillis) {
    int currentIndex = index.getAndUpdate(i -> (i + 1) % RECENT_CALLS_SIZE);
    recentCallTimes.set(currentIndex, durationMillis);
    count.incrementAndGet();
  }

  public MethodCallStats getStats() {
    int size = Math.min(count.get(), RECENT_CALLS_SIZE);
    long[] times = new long[size];
    long sum = 0;
    long min = Long.MAX_VALUE;
    long max = Long.MIN_VALUE;

    // 获取最近size次调用时间
    int startIndex = (index.get() - size + RECENT_CALLS_SIZE) % RECENT_CALLS_SIZE;
    for (int i = 0; i < size; i++) {
      int idx = (startIndex + i) % RECENT_CALLS_SIZE;
      Long time = recentCallTimes.get(idx);
      if (time != null) {
        times[i] = time;
        sum += time;
        min = Math.min(min, time);
        max = Math.max(max, time);
      }
    }

    double avg = size > 0 ? (double) sum / size : 0;
    return new MethodCallStats("", size, avg, min, max, sum, times);
  }
}
