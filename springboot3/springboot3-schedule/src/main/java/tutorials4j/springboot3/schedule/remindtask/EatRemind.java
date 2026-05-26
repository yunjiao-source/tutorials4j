package tutorials4j.springboot3.schedule.remindtask;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.schedule.Remind;

/**
 * 吃
 *
 * @author yangyunjiao
 */
@Component
public class EatRemind implements Remind {
  @Override
  public void execute() {
    if (ThreadLocalRandom.current().nextInt(99) < 50) {
      throw new RuntimeException("任务异常");
    }
    System.out.println("[吃饭提醒]主人,站起来活动一下吧" + LocalDateTime.now());
  }
}
