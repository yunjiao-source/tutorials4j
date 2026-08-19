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
 * 基于数据库的签到结果处理器。
 *
 * <p>将 {@link SignInResult} 签到结果转换为 {@link SignInResultEntity} 并异步持久化到数据库。
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseSignInResultHandler implements SignInResultHandler {
  private final SignInResultRepository signInResultRepository;

  /**
   * 将签到结果异步保存到数据库。
   *
   * @param signInResult 签到结果
   */
  @Override
  @Async
  @Transactional(rollbackFor = Exception.class)
  public void handle(SignInResult signInResult) {
    SignInResultEntity entity = new SignInResultEntity();
    BeanUtils.copyProperties(signInResult, entity);
    entity.setCreateDate(Instant.now());
    signInResultRepository.save(entity);
  }

  /**
   * 返回支持的签到数据来源（返回 null，表示不限制来源）。
   *
   * @return 数据来源
   */
  @Override
  public String sourceSupported() {
    return null;
  }

  /**
   * 是否支持处理所有数据来源的签到结果。
   *
   * @return 始终返回 true
   */
  @Override
  public boolean allSupported() {
    return true;
  }
}
