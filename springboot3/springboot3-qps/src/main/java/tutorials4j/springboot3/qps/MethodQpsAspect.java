package tutorials4j.springboot3.qps;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 对于特定方法的QPS统计，可以使用AOP切面
 *
 * @author Yun Jiao
 */
@Aspect
@Component
public class MethodQpsAspect {
    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> methodCounters = new ConcurrentHashMap<>();
    private final Map<String, Timer> methodTimers = new ConcurrentHashMap<>();

    // 存储最近100次调用时间的数据结构
    private static final int RECENT_CALLS_SIZE = 100;
    private final Map<String, CallTimeRecorder> methodCallTimes = new ConcurrentHashMap<>();

    public MethodQpsAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Around("@annotation(methodQps)")
    public Object trackQps(ProceedingJoinPoint joinPoint, MethodQps methodQps) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();

        // 获取或创建计数器
        Counter counter = methodCounters.computeIfAbsent(
                methodName,
                k -> Counter.builder("method_calls_total")
                        .tag("method", k)
                        .description("Number of method calls")
                        .register(meterRegistry)
        );

        // 获取或创建计时器
        Timer timer = methodTimers.computeIfAbsent(
                methodName,
                k -> Timer.builder("method_call_duration")
                        .tag("method", k)
                        .description("Method call duration")
                        .publishPercentiles(0.5, 0.95, 0.99) // 50%, 95%, 99%分位
                        .sla(Duration.ofMillis(10), Duration.ofMillis(50), Duration.ofMillis(100), Duration.ofMillis(500))
                        .distributionStatisticBufferLength(3)
                        .distributionStatisticExpiry(Duration.ofMinutes(2))
                        .register(meterRegistry)
        );

        // 记录调用时间
        long startTime = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.nanoTime() - startTime;
            long durationMillis = TimeUnit.NANOSECONDS.toMillis(duration);

            // 记录到Micrometer Timer
            timer.record(duration, TimeUnit.NANOSECONDS);

            // 递增计数器
            counter.increment();

            // 记录到最近100次调用时间
            CallTimeRecorder recorder = methodCallTimes.computeIfAbsent(
                    methodName,
                    k -> new CallTimeRecorder()
            );
            recorder.recordCallTime(durationMillis);
        }
    }

    /**
     * 获取最近100次调用时间的统计信息
     */
    public MethodCallStats getRecentCallStats(String methodName) {
        CallTimeRecorder recorder = methodCallTimes.get(methodName);
        if (recorder == null) {
            return new MethodCallStats(methodName, 0, 0, 0, 0, 0, new long[0]);
        }
        return recorder.getStats();
    }

    /**
     * 获取所有监控方法的统计信息
     */
    public Map<String, MethodCallStats> getAllRecentCallStats() {
        Map<String, MethodCallStats> statsMap = new ConcurrentHashMap<>();
        methodCallTimes.forEach((methodName, recorder) -> {
            statsMap.put(methodName, recorder.getStats());
        });
        return statsMap;
    }


}
