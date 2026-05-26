package tutorials4j.springboot3.schedule.simple;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.schedule.Remind;

/**
 * 运动
 *
 * @author yangyunjiao
 */
@Component
public class SportRemind implements Remind {
  @Override
  public void execute() {
    System.out.println("[运动提醒]主人,站起来活动一下吧" + LocalDateTime.now());
  }
}
