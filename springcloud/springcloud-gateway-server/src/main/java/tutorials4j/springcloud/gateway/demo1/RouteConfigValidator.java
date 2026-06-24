package tutorials4j.springcloud.gateway.demo1;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

// 路由校验器
@Component
public class RouteConfigValidator {

  public void validate(List<GatewayRouteConfig> configs) {
    Set<String> routeIds = new HashSet<>();
    for (GatewayRouteConfig config : configs) {
      if (config.getRouteId() == null || config.getRouteId().isBlank()) {
        throw new IllegalArgumentException("routeId must not be blank");
      }
      if (!routeIds.add(config.getRouteId())) {
        throw new IllegalArgumentException("duplicate routeId: " + config.getRouteId());
      }
      if (config.getServiceId() == null || config.getServiceId().isBlank()) {
        throw new IllegalArgumentException(
            "serviceId must not be blank, routeId=" + config.getRouteId());
      }
      if (config.getPaths() == null || config.getPaths().isEmpty()) {
        throw new IllegalArgumentException(
            "paths must not be empty, routeId=" + config.getRouteId());
      }
    }
  }
}
