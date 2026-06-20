package tutorials4j.framework.examples.powerjobworker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

// 单机处理器，单机执行的策略下，server 会在所有可用 worker 中选取健康度最佳的机器进行执行
@Slf4j
@Component
public class BasicProcessorDemo implements BasicProcessor {

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
