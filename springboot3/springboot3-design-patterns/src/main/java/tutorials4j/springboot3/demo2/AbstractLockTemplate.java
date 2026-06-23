package tutorials4j.springboot3.demo2;

import lombok.extern.slf4j.Slf4j;

/** 分布式锁执行模板（模板方法模式） 固定流程：初始化参数 → 尝试加锁 → 执行业务 → 释放锁 → 异常兜底 可变步骤：交给子类实现 */
@Slf4j
public abstract class AbstractLockTemplate {
  /** 模板核心方法：固定执行流程，禁止重写 */
  public final void execute() {
    LockContext context = new LockContext();
    try {
      // 1. 初始化锁参数（子类实现：自定义key、超时时间）
      initLockParam(context);
      // 2. 尝试加锁
      boolean lockResult = context.getCurrentState().lock(context);
      if (!lockResult) {
        throw new RuntimeException("获取分布式锁失败，业务终止");
      }
      // 3. 执行核心业务逻辑（子类实现）
      doBusiness();
    } catch (Exception e) {
      // 异常兜底：锁超时处理
      context.getCurrentState().timeoutHandle(context);

      log.error("分布式锁执行业务异常: {}", e.getMessage());
    } finally {
      // 4. 最终释放锁
      context.getCurrentState().unlock(context);

      log.info("上下文数据：{}", context);
    }
  }

  /** 抽象方法：初始化锁参数（可变，业务自定义） */
  protected abstract void initLockParam(LockContext context);

  /** 抽象方法：执行具体业务（可变，业务自定义） */
  protected abstract void doBusiness();
}
