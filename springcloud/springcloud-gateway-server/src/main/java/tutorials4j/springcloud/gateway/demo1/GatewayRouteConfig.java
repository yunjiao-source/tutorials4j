package tutorials4j.springcloud.gateway.demo1;

import java.util.List;
import lombok.Data;

// 路由配置模型
/**
 * 网关路由配置模型，对应 Nacos 配置文件中单个路由的配置项。
 *
 * @author Yun Jiao
 */
@Data
public class GatewayRouteConfig {

  /** 路由唯一标识。 */
  private String routeId;

  /** 路由对应的下游服务标识。 */
  private String serviceId;

  /** 路由执行顺序。 */
  private Integer order;

  /** 是否启用该路由。 */
  private Boolean enabled;

  /** 路由匹配的路径列表。 */
  private List<String> paths;

  /** 路由匹配的 HTTP 方法列表。 */
  private List<String> methods;

  /** 路由挂载的过滤器配置列表。 */
  private List<FilterConfig> filters;
  // private GrayConfig grayConfig;
  // private RateLimitConfig rateLimitConfig;
}
