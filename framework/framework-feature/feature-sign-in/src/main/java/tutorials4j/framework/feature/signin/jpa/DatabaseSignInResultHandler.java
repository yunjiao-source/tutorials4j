package tutorials4j.framework.feature.signin.jpa;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorials4j.framework.feature.signin.service.SignInResult;
import tutorials4j.framework.feature.signin.service.SignInResultHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseSignInResultHandler implements SignInResultHandler {
  private final SignInResultRepository signInResultRepository;

  @Override
  @Async
  @Transactional(rollbackFor = Exception.class)
  public void handle(SignInResult signInResult) {
    SignInResultEntity entity = new SignInResultEntity();
    BeanUtils.copyProperties(signInResult, entity);
    entity.setCreateDate(Instant.now());
    signInResultRepository.save(entity);
  }

  @Override
  public String sourceSupported() {
    return null;
  }

  @Override
  public boolean allSupported() {
    return true;
  }
}
