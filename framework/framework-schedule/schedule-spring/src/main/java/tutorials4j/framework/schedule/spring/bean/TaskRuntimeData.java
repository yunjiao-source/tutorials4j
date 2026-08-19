package tutorials4j.framework.schedule.spring.bean;

import java.time.Instant;
import lombok.Builder;

/**
 * 任务运行数据。
 *
 * <p>记录任务运行过程中的状态、时间与统计信息，供开始、完成、失败、停止等生命周期事件回调消费。
 *
 * @param taskStatus 任务状态
 * @param timestamp 数据生成时间戳
 * @param taskCode 任务编码
 * @param lotNo 批次号
 * @param totalCount 累计执行次数
 * @param totalFailureCount 累计失败次数
 * @param startTime 开始时间
 * @param endTime 结束时间
 * @param message 附加消息
 * @param throwable 异常信息
 * @author Yun Jiao
 */
@Builder
public record TaskRuntimeData(
    TaskStatusEnum taskStatus,
    Instant timestamp,
    String taskCode,
    String lotNo,
    int totalCount,
    int totalFailureCount,
    Instant startTime,
    Instant endTime,
    String message,
    Throwable throwable) {}
