package tutorials4j.framework.schedule.xxljob;

import java.time.Duration;
import java.time.Period;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_SCHEDULE_XXL_JOB)
public class XxlJobProperties {
  private boolean enabled = false;

  private AdminOptions admin = new AdminOptions();
  private ExecutorOptions executor = new ExecutorOptions();

  @Data
  public static class AdminOptions {
    // 调度中心部署根地址 [必填]：如调度中心集群部署存在多个地址则用逗号分隔。执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"；为空则关闭自动注册；
    private String addresses = "http://127.0.0.1:8081";

    // 调度中心通讯超时时间[选填]，单位秒；默认3s；
    private Duration timeout = Duration.ofSeconds(3);
  }

  @Data
  public static class ExecutorOptions {
    // 执行器启用开关 [必填]：默认开启，关闭时不进行执行器初始化；
    private boolean enabled = true;

    // 执行器AppName [必填]：执行器心跳注册分组依据；为空则关闭自动注册
    private String appName = "xxl-job-executor-sample";

    // 调度中心通讯TOKEN [必填]：安全性校验；
    private String accessToken = "default_token";

    // 执行器IP [选填]：默认为空表示自动获取IP，多网卡时可手动设置指定IP，该IP不会绑定Host仅作为通讯使用；地址信息用于 "执行器注册" 和 "调度中心请求并触发任务"；
    private String ip;

    //  执行器端口号 [选填]：小于等于0则自动获取；默认端口为9999，单机部署多个执行器时，注意要配置不同执行器端口；
    private int port = 9999;

    // 执行器注册 [选填]：优先使用该配置作为注册地址，为空时使用内嵌服务 ”IP:PORT“ 作为注册地址。从而更灵活的支持容器类型执行器动态IP和动态映射端口问题。
    private String address;

    // 执行器运行日志文件存储磁盘路径 [选填] ：需要对该路径拥有读写权限；为空则使用默认路径；
    private String logPath = "xxl-job-logs";

    // 执行器日志文件保存天数 [选填] ： 过期日志自动清理, 限制值大于等于3时生效; 否则, 如-1, 关闭自动清理功能；
    private Period logRetentionDays = Period.ofDays(30);

    // 执行器任务扫描排除路径 [选填]
    // ：Bean模式任务扫描时，忽略指定包路径，非空时生效；支持配置包路径前缀，多个逗号分隔；例如"org.package01"或"org.package01,org.package02"
    private String excludedPackage = "org.springframework";

    // 执行器GLUE模式启用开关 [选填] ：默认开启，支持全部类型任务；关闭时只支持Bean模式任务、禁用GLUE模式任务；
    private boolean glueEnabled = true;
  }
}
