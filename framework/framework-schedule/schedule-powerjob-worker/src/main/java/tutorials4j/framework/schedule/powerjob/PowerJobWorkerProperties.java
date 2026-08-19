package tutorials4j.framework.schedule.powerjob;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tech.powerjob.common.enums.Protocol;
import tech.powerjob.worker.common.constants.StoreStrategy;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * PowerJob Worker 配置属性。
 *
 * <p>对应配置前缀 {@code PropertiesConsts.PROPERTY_PREFIX_SCHEDULE_POWERJOB_WORKER}， 包含 Worker
 * 的应用名称、端口、调度中心地址、存储策略、通讯协议等启动参数。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_SCHEDULE_POWERJOB_WORKER)
public class PowerJobWorkerProperties {
  /** 是否启用 PowerJob Worker。 */
  private boolean enabled = false;

  // 宿主应用名称，需要提前在控制台完成注册	无，必填项，否则启动报错
  private String appName;
  // Worker 工作端口	27777，不推荐修改
  private int port = 27777;
  // 调度中心（powerjob-server）地址列表	无，必填项，否则启动报错
  private List<String> serverAddress;

  // 本地存储策略，枚举值磁盘/内存，大型MapReduce 等会产生大量 Task 的任务推荐使用磁盘降低内存压力，否则建议使用内存加速计算	StoreStrategy.DISK（磁盘）
  private StoreStrategy storeStrategy = StoreStrategy.DISK;
  // 与 server 的通讯协议	出于兼容性考虑仍未 AKKA，但建议手动改为 HTTP
  private Protocol protocol = Protocol.HTTP;
  // 每个Task返回结果的默认长度，超长将被截断，过长可能导致网络拥塞	8096
  private int maxResultLength = 8096;
  // 用户自定义上下文对象，该值会被透传到 TaskContext#userContext 属性（可选参数）	null
  private Map<String, Object> userContext = new HashMap<>();
  // 是否允许延迟连接 server。启用后无需Server也能顺利启动PowerJobWorker，用于本地无 server 启动等场景	false
  private boolean allowLazyConnectServer = false;
  // 单个任务向工作流上下文中追加数据的最大长度，超过这个长度会被直接丢弃	8192
  private int maxAppendedWfContextLength = 8192;
  // 同时运行的轻量级任务数量上限	1024
  private int maxLightweightTaskNum = 1024;
  // 同时运行的重量级任务数量上限	64
  private int maxHeavyweightTaskNum = 64;
  // worker 健康状态上报的间隔（秒）	10
  private Duration healthReportInterval = Duration.ofSeconds(10);

  /** Worker 标签，用于在控制台区分不同 Worker 分组。 */
  private String tag;
}
