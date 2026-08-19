package tutorials4j.framework.examples.powerjobworker;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.TaskResult;
import tech.powerjob.worker.core.processor.sdk.MapReduceProcessor;
import tech.powerjob.worker.log.OmsLogger;

// 最佳实践：MapReduce 实现静态分片
/**
 * 静态分片处理器示例：演示使用 MapReduce 实现静态分片的最佳实践。
 *
 * <p>根任务从控制台参数中解析出 KV 形式的分片配置并派发子任务，子任务（{@link SubTask}）执行实际业务处理。
 *
 * @author Yun Jiao
 */
@Component
public class StaticSliceProcessor implements MapReduceProcessor {

  /**
   * 处理任务：根任务负责解析分片参数并派发子任务，非根任务模拟执行分片对应的实际业务处理。
   *
   * @param context 任务上下文，根任务时包含分片参数，子任务时包含派发的 {@link SubTask}
   * @return 根任务返回派发成功结果；子任务返回对应分片的处理结果
   * @throws Exception 任务处理过程中可能抛出的异常
   */
  @Override
  public ProcessResult process(TaskContext context) throws Exception {
    OmsLogger omsLogger = context.getOmsLogger();

    // root task 负责分发任务
    if (isRootTask()) {
      // 从控制台传递分片参数，假设格式为KV：1=a&2=b&3=c
      String jobParams = context.getJobParams();
      Map<String, String> paramsMap = Splitter.on("&").withKeyValueSeparator("=").split(jobParams);

      List<SubTask> subTasks = Lists.newLinkedList();
      paramsMap.forEach((k, v) -> subTasks.add(new SubTask(Integer.parseInt(k), v)));
      map(subTasks, "SLICE_TASK");
      return new ProcessResult(true, "ROOT_PROCESS_SUCCESS");
    }

    Object subTask = context.getSubTask();
    if (subTask instanceof SubTask) {
      // 实际处理
      // 当然，如果觉得 subTask 还是很大，也可以继续分发哦

      return new ProcessResult(
          true, "subTask:" + ((SubTask) subTask).getIndex() + " process successfully");
    }
    return new ProcessResult(false, "UNKNOWN BUG");
  }

  /**
   * 汇总所有子任务的执行结果，可按需求做统计工作；若不需要统计，直接使用 Map 处理器即可。
   *
   * @param context 任务上下文
   * @param taskResults 所有子任务的执行结果列表
   * @return 汇总结果，示例中固定返回成功
   */
  @Override
  public ProcessResult reduce(TaskContext context, List<TaskResult> taskResults) {
    // 按需求做一些统计工作... 不需要的话，直接使用 Map 处理器即可
    return new ProcessResult(true, "xxxx");
  }

  /** 静态分片子任务：携带分片索引与分片参数 */
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SubTask {

    /** 分片索引 */
    private int index;

    /** 分片对应的参数 */
    private String params;
  }
}
