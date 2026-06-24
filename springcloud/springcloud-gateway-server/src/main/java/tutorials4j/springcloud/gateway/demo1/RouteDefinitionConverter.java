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
@Slf4j
@Component
public class RouteDefinitionConverter {

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

  private PredicateDefinition buildPathPredicate(List<String> paths) {
    PredicateDefinition predicate = new PredicateDefinition();
    predicate.setName("Path");
    predicate.addArg("_genkey_0", String.join(",", paths));
    return predicate;
  }

  private PredicateDefinition buildMethodPredicate(List<String> methods) {
    PredicateDefinition predicate = new PredicateDefinition();
    predicate.setName("Method");
    predicate.addArg("_genkey_0", String.join(",", methods));
    return predicate;
  }
}
