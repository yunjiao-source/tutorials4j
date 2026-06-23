package tutorials4j.springboot3.demo2;

/** 分布式锁状态接口（状态模式顶层抽象） 定义所有锁状态通用行为：加锁、释放锁、超时处理 */
public interface LockState {
  /**
   * 加锁行为
   *
   * @param lockContext 锁上下文
   * @return 加锁是否成功
   */
  boolean lock(LockContext lockContext);

  /**
   * 释放锁行为
   *
   * @param lockContext 锁上下文
   * @return 释放是否成功
   */
  boolean unlock(LockContext lockContext);

  /**
   * 锁超时处理行为
   *
   * @param lockContext 锁上下文
   */
  void timeoutHandle(LockContext lockContext);
}
