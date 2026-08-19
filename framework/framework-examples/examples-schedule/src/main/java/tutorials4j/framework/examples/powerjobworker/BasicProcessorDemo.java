package tutorials4j.framework.examples.powerjobworker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

// 单机处理器，单机执行的策略下，server 会在所有可用 worker 中选取健康度最佳的机器进行执行
/**
 * 单机处理器示例：演示 PowerJob 单机执行策略下的任务处理。
 *
 * <p>在单机执行策略下，PowerJob server 会在所有可用 worker 中选取健康度最佳的机器来执行该任务。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class BasicProcessorDemo implements BasicProcessor {

  /**
   * 执行单机任务：通过在线日志记录任务参数，并返回处理成功的结果。
   *
   * @param context 任务上下文，包含任务参数与在线日志等
   * @return 处理结果，固定为成功
   * @throws Exception 任务处理过程中可能抛出的异常
   */
  @Override
  public ProcessResult process(TaskContext context) throws Exception {
    // 在线日志功能，可以直接在控制台查看任务日志，非常便捷
    OmsLogger omsLogger = context.getOmsLogger();
    omsLogger.info("单机处理器: {}", context.getJobParams());
    log.info("单机处理器: {}", context);
    // 返回结果，该结果会被持久化到数据库，在前端页面直接查看，极为方便
    return new ProcessResult(true, "单机处理器 任务处理完成");
  }
}
