package tutorials4j.framework.schedule.spring.bean;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import lombok.Builder;

/**
 * 任务执行详情。
 *
 * <p>汇总任务定义信息与累计执行过程中的运行数据，用于任务监控与展示。
 *
 * @param taskCode 任务编码
 * @param classSimpleName 任务类简单名
 * @param cron cron 表达式
 * @param enabled 是否启用
 * @param description 任务描述
 * @param metadata 任务元数据
 * @param initialDelay 首次执行延时
 * @param maxFailureCount 最大失败次数
 * @param maxExecutionCount 最大执行次数
 * @param dueDate 任务结束日期
 * @param taskStatus 任务状态
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
public record TaskExecutionDetails(
    String taskCode,
    String classSimpleName,
    String cron,
    boolean enabled,
    String description,
    Map<String, String> metadata,
    Duration initialDelay,
    Integer maxFailureCount,
    Integer maxExecutionCount,
    Instant dueDate,
    TaskStatusEnum taskStatus,
    String lotNo,
    int totalCount,
    int totalFailureCount,
    Instant startTime,
    Instant endTime,
    String message,
    Throwable throwable) {}
