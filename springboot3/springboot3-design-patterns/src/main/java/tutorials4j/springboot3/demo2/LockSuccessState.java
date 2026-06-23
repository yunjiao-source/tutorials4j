package tutorials4j.springboot3.demo2;

/** 锁定成功状态：持有锁，可执行业务，支持解锁、超时 */
public class LockSuccessState implements LockState {
  @Override
  public boolean lock(LockContext lockContext) {
    // 支持锁重入：当前线程持有锁，直接返回成功
    if (Thread.currentThread().getName().equals(lockContext.getHoldThread())) {
      return true;
    }
    // 其他线程加锁失败
    return false;
  }

  @Override
  public boolean unlock(LockContext lockContext) {
    // 释放Redis锁
    boolean unLockResult = RedisLockUtil.unLock(lockContext.getLockKey());
    if (unLockResult) {
      // 解锁成功，切换为已释放状态
      lockContext.setCurrentState(LockContext.LOCK_RELEASE_STATE);
      // 清空持有线程
      lockContext.setHoldThread(null);
    }
    return unLockResult;
  }

  @Override
  public void timeoutHandle(LockContext lockContext) {
    // 锁超时，强制释放锁，切换为超时状态
    RedisLockUtil.unLock(lockContext.getLockKey());
    lockContext.setCurrentState(LockContext.LOCK_TIMEOUT_STATE);
    lockContext.setHoldThread(null);
  }
}
