package tutorials4j.springboot3.demo2;

import lombok.Data;

/** 分布式锁上下文（单例模式：全局唯一） 统一管理锁状态、参数、线程信息 */
@Data
public class LockContext {
  // 预创建所有状态实例（全局复用，避免重复创建对象）
  public static final LockState UN_LOCK_STATE = new UnLockState();
  public static final LockState LOCK_SUCCESS_STATE = new LockSuccessState();
  public static final LockState LOCK_TIMEOUT_STATE = new LockTimeoutState();
  public static final LockState LOCK_RELEASE_STATE = new LockReleaseState();
  // 当前锁状态
  private LockState currentState;
  // 锁的key（业务唯一标识）
  private String lockKey;
  // 锁超时时间
  private long timeout;
  // 持有锁的线程
  private String holdThread;

  public LockContext() {
    // 初始状态为未锁定
    this.currentState = UN_LOCK_STATE;
  }
}
