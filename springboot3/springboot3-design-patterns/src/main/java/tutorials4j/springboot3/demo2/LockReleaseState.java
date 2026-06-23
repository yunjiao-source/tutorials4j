package tutorials4j.springboot3.demo2;

/** 锁已释放状态：锁已主动释放，可重新加锁 */
public class LockReleaseState implements LockState {
  @Override
  public boolean lock(LockContext lockContext) {
    boolean lockResult = RedisLockUtil.tryLock(lockContext.getLockKey(), lockContext.getTimeout());
    if (lockResult) {
      lockContext.setCurrentState(LockContext.LOCK_SUCCESS_STATE);
      lockContext.setHoldThread(Thread.currentThread().getName());
    }
    return lockResult;
  }

  @Override
  public boolean unlock(LockContext lockContext) {
    // 锁已释放，无需重复解锁
    return true;
  }

  @Override
  public void timeoutHandle(LockContext lockContext) {
    // 已释放，无超时逻辑
  }
}
