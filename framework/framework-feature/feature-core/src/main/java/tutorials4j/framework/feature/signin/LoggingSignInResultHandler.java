package tutorials4j.framework.feature.signin;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class LoggingSignInResultHandler implements SignInResultHandler {

  @Override
  public void handle(SignInResult event) {
    log.info("签到结果: {}", event);
  }
}
