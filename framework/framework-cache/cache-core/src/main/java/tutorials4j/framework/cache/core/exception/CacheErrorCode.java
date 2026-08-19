package tutorials4j.framework.cache.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * 缓存模块错误码枚举。
 *
 * <p>集中定义缓存模块可能出现的业务错误码及提示信息，供异常抛出与统一反馈使用。
 *
 * @author Yun Jiao
 */
@Getter
public enum CacheErrorCode implements ErrorCode {
  /** 缓存管理器创建者不存在 */
  CACHE_MANAGER_CREATOR_NOT_EXIST("缓存管理器创建者不存在"),
  /** 获取锁失败 */
  CACHE_ACCQUIRE_LOCK_FAILURE("获取锁失败"),
  /** 释放锁失败 */
  CACHE_RELEASE_LOCK_FAILURE("释放锁失败"),
  ;

  private final Feedback feedback;

  CacheErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
