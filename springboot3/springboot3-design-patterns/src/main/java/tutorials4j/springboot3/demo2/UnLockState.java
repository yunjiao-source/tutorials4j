package tutorials4j.springboot3.demo2;

/** 未锁定状态：初始状态，可执行加锁操作 */
public class UnLockState implements LockState {
  @Override
  public boolean lock(LockContext lockContext) {
    // 执行Redis分布式锁加锁逻辑（核心）
    boolean lockResult = RedisLockUtil.tryLock(lockContext.getLockKey(), lockContext.getTimeout());
    if (lockResult) {
      // 加锁成功，切换状态为锁定成功
      lockContext.setCurrentState(LockContext.LOCK_SUCCESS_STATE);
    }
    return lockResult;
  }

  @Override
  public boolean unlock(LockContext lockContext) {
    // 未加锁状态，无需释放锁，直接返回成功
    return true;
  }

  @Override
  public void timeoutHandle(LockContext lockContext) {
    // 未加锁状态，无超时逻辑，无需处理
  }
}
