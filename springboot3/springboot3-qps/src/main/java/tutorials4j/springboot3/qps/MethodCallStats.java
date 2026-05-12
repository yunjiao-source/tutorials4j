package tutorials4j.springboot3.qps;

import java.util.Arrays;

/**
 * 方法调用统计信息DTO
 *
 * @author Yun Jiao
 */
public record MethodCallStats(
        String methodName,
        int callCount,  // 实际记录次数
        double avgTime, // 平均调用时间(ms)
        long minTime,   // 最短调用时间(ms)
        long maxTime,   // 最长调用时间(ms)
        long totalTime, // 总调用时间(ms)
        long[]recentCallTimes // 最近的具体调用时间数组
) {

    @Override
    public String toString() {
        return "MethodCallStats{" +
                "methodName='" + methodName + '\'' +
                ", callCount=" + callCount +
                ", avgTime=" + avgTime +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                ", totalTime=" + totalTime +
                ", recentCallTimes=" + Arrays.toString(recentCallTimes) +
                '}';
    }
}
