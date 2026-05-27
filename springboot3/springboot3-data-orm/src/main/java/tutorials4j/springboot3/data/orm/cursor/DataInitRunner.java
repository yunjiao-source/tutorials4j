package tutorials4j.springboot3.data.orm.cursor;

import com.github.javafaker.Faker;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DataInitRunner implements CommandLineRunner {
  private final CursorUserRepository cursorUserRepository;
  private final Faker faker = new Faker();

  @Override
  public void run(String... args) throws Exception {
    if (cursorUserRepository.count() > 1000) {
      return;
    }
    List<CursorUser> users = new ArrayList<>();
    LocalDateTime start = LocalDateTime.now();

    IntStream.range(0, 20000)
        .forEach(
            i -> {
              CursorUser user = new CursorUser();
              user.setName(faker.name().username());
              user.setCreatedAt(start.plusSeconds(i));
              users.add(user);
            });

    cursorUserRepository.saveAll(users);
  }
}
