package tutorials4j.framework.cache.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public enum CacheErrorCode implements ErrorCode {
  CACHE_MANAGER_CREATOR_NOT_EXIST("缓存管理器创建者不存在"),
  CACHE_ACCQUIRE_LOCK_FAILURE("获取锁失败"),
  CACHE_RELEASE_LOCK_FAILURE("释放锁失败"),
  ;

  private final Feedback feedback;

  CacheErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
