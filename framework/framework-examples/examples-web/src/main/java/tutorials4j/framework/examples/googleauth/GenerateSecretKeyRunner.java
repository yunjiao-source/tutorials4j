package tutorials4j.framework.examples.googleauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tutorials4j.framework.web.security.google.GoogleAuthService;

/**
 * 生成 Secret Key
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GenerateSecretKeyRunner implements CommandLineRunner {
  private final GoogleAuthService googleAuthService;

  @Override
  public void run(String... args) throws Exception {
    log.info("key1: {}", googleAuthService.generateSecretKey());
    log.info("key2: {}", googleAuthService.generateSecretKey());
  }
}
