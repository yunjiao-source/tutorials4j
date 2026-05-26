package tutorials4j.springboot3.schedule;

/**
 * 调度任务的抽象封装
 *
 * @author Yun Jiao
 */
public interface Remind {
  /** 提醒任务执行逻辑 */
  void execute();
}
