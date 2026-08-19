package tutorials4j.framework.schedule.spring.handler;

import tutorials4j.framework.schedule.spring.bean.TaskRuntimeData;

/**
 * 任务运行数据处理器。
 *
 * <p>函数式接口，用于消费定时任务的运行数据（如创建、启动、完成、异常等事件）， 便于扩展监控、日志、告警等能力。
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface TaskRuntimeDataHandler {
  /**
   * 处理一条任务运行数据。
   *
   * @param data 任务运行数据
   */
  void handle(TaskRuntimeData data);
}
