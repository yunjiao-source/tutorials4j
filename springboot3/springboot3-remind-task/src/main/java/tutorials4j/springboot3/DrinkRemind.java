package tutorials4j.springboot3;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.task.Remind;

/**
 * 喝水
 *
 * @author yangyunjiao
 */
@Component
public class DrinkRemind implements Remind {
  @Override
  public void execute() {
    System.out.println("[喝水提醒]主人,记得喝水哦" + LocalDateTime.now());
  }
}
