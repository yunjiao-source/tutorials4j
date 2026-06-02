package tutorials4j.framework.examples.uid;

import java.util.stream.IntStream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tutorials4j.framework.common.uid.UidUtils;

/**
 * 实例
 *
 * @author Yun Jiao
 */
@Component
public class DemoRunner implements CommandLineRunner {

  @Override
  public void run(String... args) throws Exception {
    System.out.println(">>>DEFAULTED");
    IntStream.range(0, 10)
        .forEach(i -> System.out.println(i + ": " + UidUtils.DEFAULTED.nextUid()));
    System.out.println(">>>CACHED");
    IntStream.range(0, 10).forEach(i -> System.out.println(i + ": " + UidUtils.CACHED.nextUid()));
  }
}
