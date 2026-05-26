package tutorials4j.springboot3.schedule.remindtask;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.common.jpa.RemindTask;
import tutorials4j.springboot3.common.jpa.RemindTaskRepository;

/**
 * 测试数据
 *
 * @author yangyunjiao
 */
@Component
@RequiredArgsConstructor
public class DataInitRunner implements CommandLineRunner {
  private final RemindTaskRepository remindTaskRepository;

  @Override
  public void run(String... args) throws Exception {
    if (remindTaskRepository.findAll().isEmpty()) {
      remindTaskRepository.save(
          new RemindTask("喝水提醒", "0/3 * * * * ?", DrinkRemind.class.getName()));
      remindTaskRepository.save(
          new RemindTask("运动提醒", "0/4 * * * * ?", SportRemind.class.getName()));
      remindTaskRepository.save(new RemindTask("吃饭提醒", "0/5 * * * * ?", EatRemind.class.getName()));
    }
  }
}
