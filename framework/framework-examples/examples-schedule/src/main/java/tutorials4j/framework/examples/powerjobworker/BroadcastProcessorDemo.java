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
/**
 * 广播处理器示例：演示 PowerJob 广播执行策略下的任务处理。
 *
 * <p>广播执行策略下，集群中的所有机器都会被调度执行该任务；在 {@link BasicProcessor} 的基础上额外提供 preProcess 与 postProcess
 * 钩子，分别在整个集群开始之前/结束之后由一台机器执行，便于资源的准备和释放。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class BroadcastProcessorDemo implements BroadcastProcessor {

  /**
   * 集群所有 worker 执行 {@link #process} 前的预执行钩子，负责资源准备等前置逻辑。
   *
   * @param taskContext 任务上下文
   * @return 前置处理结果，固定为成功
   * @throws Exception 前置处理过程中可能抛出的异常
   */
  @Override
  public ProcessResult preProcess(TaskContext taskContext) throws Exception {
    log.info("广播执行的策略 前置处理完成");
    // 预执行，会在所有 worker 执行 process 方法前调用
    return new ProcessResult(true, "广播执行的策略 前置处理完成");
  }

  /**
   * 所有 worker 集群都会执行的业务处理逻辑。
   *
   * @param context 任务上下文
   * @return 处理结果，固定为成功
   * @throws Exception 处理过程中可能抛出的异常
   */
  @Override
  public ProcessResult process(TaskContext context) throws Exception {
    log.info("广播执行的策略 处理完成, {}", context);
    // 撰写整个worker集群都会执行的代码逻辑
    return new ProcessResult(true, "广播执行的策略 处理完成");
  }

  /**
   * 集群所有 worker 执行完 {@link #process} 后的收尾钩子，该结果将作为任务的最终执行结果。
   *
   * @param taskContext 任务上下文
   * @param taskResults 所有 worker 的任务执行结果列表
   * @return 后置处理结果，固定为成功
   * @throws Exception 后置处理过程中可能抛出的异常
   */
  @Override
  public ProcessResult postProcess(TaskContext taskContext, List<TaskResult> taskResults)
      throws Exception {
    log.info("广播执行的策略 后置处理完成, {}", taskResults);

    // 收尾，会在所有 worker 执行完毕 process 方法后调用，该结果将作为最终的执行结果
    return new ProcessResult(true, "广播执行的策略 后置处理完成");
  }
}
