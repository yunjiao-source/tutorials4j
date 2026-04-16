package tutorials4j.springboot3.scaler;

import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 连接池管理接口
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/pool")
@Slf4j
@RequiredArgsConstructor
public class PoolController {
    private final ConnectionPoolMonitor poolMonitor;

    private final HikariDataSource dataSource;

    private final PoolScalerComponent poolScalerComponent;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();

        PoolStatistics current = poolMonitor.getCurrentMetrics();
        if (current != null) {
            status.put("current", current);
        }

        HikariConfigMXBean configMXBean = dataSource.getHikariConfigMXBean();
        status.put("maxPoolSize", configMXBean.getMaximumPoolSize());
        status.put("minIdle", configMXBean.getMinimumIdle());

        return ResponseEntity.ok(status);
    }

    @GetMapping("/metrics/history")
    public ResponseEntity<List<PoolStatistics>> getMetricsHistory() {
        return ResponseEntity.ok(poolMonitor.getRecentMetrics());
    }

    @PostMapping("/scale")
    public ResponseEntity<String> manualScale(@RequestParam("targetSize") int targetSize) {
        try {
            HikariConfigMXBean configMXBean = dataSource.getHikariConfigMXBean();
            int currentSize = configMXBean.getMaximumPoolSize();

            configMXBean.setMaximumPoolSize(targetSize);

            return ResponseEntity.ok(String.format("连接池已调整: %d -> %d",
                    currentSize, targetSize));
        } catch (Exception e) {
            log.error("手动调整连接池失败", e);
            return ResponseEntity.badRequest().body("调整失败: " + e.getMessage());
        }
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        Map<String, Object> config = new HashMap<>();

        HikariConfigMXBean configMXBean = dataSource.getHikariConfigMXBean();
        config.put("maximumPoolSize", configMXBean.getMaximumPoolSize());
        config.put("minimumIdle", configMXBean.getMinimumIdle());
        config.put("connectionTimeout", configMXBean.getConnectionTimeout());
        config.put("idleTimeout", configMXBean.getIdleTimeout());
        config.put("maxLifetime", configMXBean.getMaxLifetime());

        return ResponseEntity.ok(config);
    }
}
