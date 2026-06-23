package tutorials4j.springboot3.demo2;

/** 锁超时状态：锁超时失效，不可重入，可重置状态 */
public class LockTimeoutState implements LockState {
  @Override
  public boolean lock(LockContext lockContext) {
    // 超时状态可重新尝试加锁
    boolean lockResult = RedisLockUtil.tryLock(lockContext.getLockKey(), lockContext.getTimeout());
    if (lockResult) {
      lockContext.setCurrentState(LockContext.LOCK_SUCCESS_STATE);
      lockContext.setHoldThread(Thread.currentThread().getName());
    }
    return lockResult;
  }

  @Override
  public boolean unlock(LockContext lockContext) {
    // 锁已超时失效，无需重复释放
    return true;
  }

  @Override
  public void timeoutHandle(LockContext lockContext) {
    // 已处于超时状态，无需重复处理
  }
}
