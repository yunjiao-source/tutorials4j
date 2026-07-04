package tutorials4j.framework.cache.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.feedback.Feedback;
import tutorials4j.framework.common.core.exception.feedback.NotAcceptableFeedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public enum CacheErrorCode implements ErrorCode {
  CACHE_MANAGER_CREATOR_NOT_EXIST(new NotAcceptableFeedback("缓存管理器创建者不存在")),
  CACHE_ACCQUIRE_LOCK_FAILURE(new NotAcceptableFeedback("获取锁失败")),
  CACHE_RELEASE_LOCK_FAILURE(new NotAcceptableFeedback("释放锁失败")),
  ;

  private final Feedback feedback;

  CacheErrorCode(Feedback feedback) {
    this.feedback = feedback;
    feedback.setCode(this.name());
  }
}
