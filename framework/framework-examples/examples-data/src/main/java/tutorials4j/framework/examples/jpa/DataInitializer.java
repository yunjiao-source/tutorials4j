package tutorials4j.framework.examples.jpa;

import com.github.javafaker.Faker;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tutorials4j.framework.examples.SexEnum;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final Faker faker = new Faker();

  @Override
  public void run(String... args) {
    long count = userRepository.count();
    if (count >= 100) {
      return;
    }

    IntStream.range(0, 100)
        .forEach(
            i -> {
              User user = new User();
              user.setUsername(faker.name().username());
              user.setEmail(user.getUsername() + "@example.com");
              user.setAge(faker.number().numberBetween(1, 100));
              user.setSex(faker.options().option(SexEnum.values()));

              int orderCount = ThreadLocalRandom.current().nextInt(3);
              for (int j = 1; j <= orderCount; j++) {
                Order order = new Order();
                order.setOrderNumber("ORD-" + faker.idNumber().ssnValid());
                order.setAmount(BigDecimal.valueOf(100 + i * 10 + j));
                order.setOrderTime(LocalDateTime.now().minusHours(j));
                order.setUser(user);
                user.getOrders().add(order);
              }
              userRepository.save(user);
            });
  }
}
