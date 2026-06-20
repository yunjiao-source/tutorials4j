package tutorials4j.framework.examples.powerjobworker;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.TaskResult;
import tech.powerjob.worker.core.processor.sdk.BroadcastProcessor;

// 广播执行的策略下，所有机器都会被调度执行该任务。为了便于资源的准备和释放，广播处理器在BasicProcessor 的基础上额外增加了 preProcess 和
// postProcess 方法，分别在整个集群开始之前/结束之后选一台机器执行相关方法
@Slf4j
@Component
public class BroadcastProcessorDemo implements BroadcastProcessor {

  @Override
  public ProcessResult preProcess(TaskContext taskContext) throws Exception {
    log.info("广播执行的策略 前置处理完成");
    // 预执行，会在所有 worker 执行 process 方法前调用
    return new ProcessResult(true, "广播执行的策略 前置处理完成");
  }

  @Override
  public ProcessResult process(TaskContext context) throws Exception {
    log.info("广播执行的策略 处理完成, {}", context);
    // 撰写整个worker集群都会执行的代码逻辑
    return new ProcessResult(true, "广播执行的策略 处理完成");
  }

  @Override
  public ProcessResult postProcess(TaskContext taskContext, List<TaskResult> taskResults)
      throws Exception {
    log.info("广播执行的策略 后置处理完成, {}", taskResults);

    // 收尾，会在所有 worker 执行完毕 process 方法后调用，该结果将作为最终的执行结果
    return new ProcessResult(true, "广播执行的策略 后置处理完成");
  }
}
