package tutorials4j.framework.feature.signin.domain;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorials4j.framework.feature.signin.SignInResult;
import tutorials4j.framework.feature.signin.SignInResultHandler;

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
  @Transactional(rollbackFor = Exception.class)
  public void handle(SignInResult signInResult) {
    if (log.isDebugEnabled()) {
      log.debug("签到结果：{}", signInResult);
    }

    SignInResultEntity entity = new SignInResultEntity();
    BeanUtils.copyProperties(signInResult, entity);
    entity.setCreateDate(Instant.now());
    signInResultRepository.save(entity);
  }
}
