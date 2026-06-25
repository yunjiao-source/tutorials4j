package tutorials4j.springboot3.demo1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DemoRunner implements CommandLineRunner {
  private final UserService userService;

  @Override
  public void run(String... args) throws Exception {
    userService.createUser();
  }
}
