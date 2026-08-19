package tutorials4j.framework.examples.totp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tutorials4j.framework.web.security.totp.GoogleAuthService;

/**
 * 应用启动时生成并打印 TOTP 密钥的示例运行器。
 *
 * <p>利用 {@link GoogleAuthService#generateSecretKey()} 生成随机密钥并输出到日志， 用于演示如何获取 TOTP 注册所需的 Secret Key。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GenerateSecretKeyRunner implements CommandLineRunner {
  private final GoogleAuthService googleAuthService;

  /** 启动时生成两个随机 TOTP Secret Key 并打印到日志。 */
  @Override
  public void run(String... args) throws Exception {
    log.info("key1: {}", googleAuthService.generateSecretKey());
    log.info("key2: {}", googleAuthService.generateSecretKey());
  }
}
