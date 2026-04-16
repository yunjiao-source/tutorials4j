package tutorials4j.springboot3.scaler;

import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 连接池监控器
 *
 * @author Yun Jiao
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ConnectionPoolMonitor {
    private final HikariDataSource dataSource;

    private final DynamicPoolProperties properties;

    private final PoolScalerComponent poolScalerComponent;

    private final NotificationService notificationService;

    private final Queue<PoolStatistics> metricsHistory = new ConcurrentLinkedQueue<>();

    @Scheduled(fixedRateString = "${dynamic.pool.monitor.interval-time:10s}")
    public void monitor() {
        if (!properties.isEnabled()) {
            return;
        }

        try {
            HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();
            HikariConfigMXBean configMXBean = dataSource.getHikariConfigMXBean();

            if (poolMXBean == null) {
                return;
            }

            PoolStatistics stats = collectMetrics(poolMXBean, configMXBean);
            metricsHistory.offer(stats);

            // 清理过期数据
            cleanupOldMetrics();

            // 记录日志
            log.debug("连接池状态: 活跃={}, 空闲={}, 等待={}, 总数={}",
                    stats.getActiveConnections(),
                    stats.getIdleConnections(),
                    stats.getPendingThreads(),
                    stats.getTotalConnections());

            // 检查是否需要扩缩容
            checkAndScale(stats);

            // 检查告警条件
            checkAlerts(stats);

        } catch (Exception e) {
            log.error("连接池监控失败", e);
        }
    }

    private PoolStatistics collectMetrics(HikariPoolMXBean poolMXBean,
                                          HikariConfigMXBean configMXBean) {
        return PoolStatistics.builder()
                .timestamp(System.currentTimeMillis())
                .activeConnections(poolMXBean.getActiveConnections())
                .idleConnections(poolMXBean.getIdleConnections())
                .totalConnections(poolMXBean.getTotalConnections())
                .pendingThreads(poolMXBean.getThreadsAwaitingConnection())
                .maxPoolSize(configMXBean.getMaximumPoolSize())
                .minIdleConnections(configMXBean.getMinimumIdle())
                .usagePercentage(calculateUsagePercentage(poolMXBean, configMXBean))
                .build();
    }

    private double calculateUsagePercentage(HikariPoolMXBean poolMXBean,
                                            HikariConfigMXBean configMXBean) {
        int maxSize = configMXBean.getMaximumPoolSize();
        if (maxSize == 0) {
            return 0;
        }
        return (double) poolMXBean.getActiveConnections() / maxSize * 100;
    }

    private void cleanupOldMetrics() {
        long cutoff = System.currentTimeMillis() -
                properties.getMonitor().getMetricsRetentionTime().toMillis();

        while (!metricsHistory.isEmpty() &&
                metricsHistory.peek().getTimestamp() < cutoff) {
            metricsHistory.poll();
        }
    }

    private void checkAndScale(PoolStatistics stats) {
        ScaleDecision decision = poolScalerComponent.evaluate(stats, getRecentMetrics());

        if (decision.isShouldScale()) {
            log.info("触发连接池{}: 当前={}, 目标={}, 原因={}",
                    decision.getScaleType() == ScaleDecision.ScaleType.UP ? "扩容" : "缩容",
                    stats.getMaxPoolSize(),
                    decision.getTargetPoolSize(),
                    decision.getReason());

            boolean success = poolScalerComponent.executeScale(decision);

            if (success) {
                notificationService.notifyScale(decision, stats);
            }
        }
    }

    private void checkAlerts(PoolStatistics stats) {
        // 高使用率告警
        if (stats.getUsagePercentage() > properties.getAlert().getHighUsageThreshold()) {
            notificationService.notifyHighUsage(stats);
        }

        // 高等待队列告警
        if (stats.getPendingThreads() > 0) {
            notificationService.notifyQueueWait(stats);
        }
    }

    public List<PoolStatistics> getRecentMetrics() {
        return new ArrayList<>(metricsHistory);
    }

    public PoolStatistics getCurrentMetrics() {
        List<PoolStatistics> metrics = getRecentMetrics();
        return metrics.isEmpty() ? null : metrics.get(metrics.size() - 1);
    }
}
