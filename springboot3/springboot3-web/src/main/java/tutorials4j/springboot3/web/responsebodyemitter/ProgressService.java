package tutorials4j.springboot3.web.responsebodyemitter;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;

/**
 * 进度服务
 *
 * @author Yun Jiao
 */
@Service
public class ProgressService {

  public void startMonitoring(String taskId, Consumer<Progress> progress) {
    IntStream.range(1, 9)
        .forEach(
            i -> {
              randomSleep();
              progress.accept(new Progress(10 * i, "", false));
            });

    randomSleep();
    progress.accept(new Progress(100, "完成", true));
  }

  private void randomSleep() {
    long milli = ThreadLocalRandom.current().nextInt(1000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
