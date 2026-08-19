package tutorials4j.springcloud.gateway.demo1;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

// 路由转换器
/**
 * 路由配置转换器：将 {@link GatewayRouteConfig} 配置模型转换为网关的 {@link RouteDefinition}。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class RouteDefinitionConverter {

  /**
   * 将路由配置模型转换为网关路由定义，生成 lb 协议的目标地址、Path/Method 断言与过滤器定义。
   *
   * @param config 路由配置模型
   * @return 转换后的网关路由定义
   */
  public RouteDefinition convert(GatewayRouteConfig config) {
    RouteDefinition definition = new RouteDefinition();
    definition.setId(config.getRouteId());
    definition.setOrder(config.getOrder() == null ? 0 : config.getOrder());
    definition.setUri(URI.create("lb://" + config.getServiceId()));

    List<PredicateDefinition> predicates = new ArrayList<>();
    predicates.add(buildPathPredicate(config.getPaths()));
    if (config.getMethods() != null && !config.getMethods().isEmpty()) {
      predicates.add(buildMethodPredicate(config.getMethods()));
    }
    definition.setPredicates(predicates);

    List<FilterDefinition> filters = new ArrayList<>();
    if (config.getFilters() != null) {
      for (FilterConfig filter : config.getFilters()) {
        FilterDefinition filterDefinition = new FilterDefinition();
        filterDefinition.setName(filter.getName());
        filterDefinition.setArgs(filter.getArgs());
        filters.add(filterDefinition);
      }
    }
    definition.setFilters(filters);
    return definition;
  }

  /**
   * 构建按路径匹配的 Path 断言。
   *
   * @param paths 匹配的路径列表
   * @return Path 断言定义
   */
  private PredicateDefinition buildPathPredicate(List<String> paths) {
    PredicateDefinition predicate = new PredicateDefinition();
    predicate.setName("Path");
    predicate.addArg("_genkey_0", String.join(",", paths));
    return predicate;
  }

  /**
   * 构建按 HTTP 方法匹配的 Method 断言。
   *
   * @param methods 匹配的 HTTP 方法列表
   * @return Method 断言定义
   */
  private PredicateDefinition buildMethodPredicate(List<String> methods) {
    PredicateDefinition predicate = new PredicateDefinition();
    predicate.setName("Method");
    predicate.addArg("_genkey_0", String.join(",", methods));
    return predicate;
  }
}
