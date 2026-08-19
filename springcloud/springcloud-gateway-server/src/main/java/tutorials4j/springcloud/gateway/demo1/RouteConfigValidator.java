package tutorials4j.springcloud.gateway.demo1;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

// 路由校验器
/**
 * 路由配置校验器：校验路由配置的合法性，包括 routeId 非空且唯一、serviceId 与 paths 非空。
 *
 * @author Yun Jiao
 */
@Component
public class RouteConfigValidator {

  /**
   * 校验路由配置列表。
   *
   * @param configs 待校验的路由配置列表
   * @throws IllegalArgumentException 当 routeId 为空或重复、serviceId 为空、paths 为空时抛出
   */
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
