package tutorials4j.springcloud.gateway.demo1;

import java.util.List;
import lombok.Data;

// 路由配置模型
@Data
public class GatewayRouteConfig {

  private String routeId;
  private String serviceId;
  private Integer order;
  private Boolean enabled;
  private List<String> paths;
  private List<String> methods;
  private List<FilterConfig> filters;
  // private GrayConfig grayConfig;
  // private RateLimitConfig rateLimitConfig;
}
