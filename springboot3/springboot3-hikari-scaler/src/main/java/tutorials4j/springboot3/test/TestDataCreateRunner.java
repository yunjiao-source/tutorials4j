package tutorials4j.springboot3.test;

import com.github.javafaker.Faker;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.User;
import tutorials4j.springboot3.UserRepository;

/**
 * 测试数据生成
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class TestDataCreateRunner implements CommandLineRunner {
  private final Faker faker = new Faker();
  private final UserRepository userRepository;

  @Override
  public void run(String... args) throws Exception {
    int maxRowCount = 1000;
    long count = userRepository.count();
    if (count < maxRowCount) {
      IntStream.range(0, maxRowCount)
          .forEach(
              i -> {
                User user = new User();
                user.setName(faker.name().fullName());
                user.setAge(faker.number().numberBetween(10, 100));
                userRepository.save(user);
              });
    }
  }
}
