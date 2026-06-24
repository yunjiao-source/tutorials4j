package tutorials4j.springcloud.gateway.demo1;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// 基于 Nacos 的生产级动态路由实现
@Slf4j
@Component
@RequiredArgsConstructor
public class NacosRouteDefinitionRepository implements RouteDefinitionRepository {

  private static final String DATA_ID = "gateway-routes.json";
  private static final String GROUP = "GATEWAY_GROUP";

  private final NacosConfigManager nacosConfigManager;
  private final ObjectMapper objectMapper;
  private final RouteDefinitionConverter routeDefinitionConverter;
  private final RouteConfigValidator routeConfigValidator;
  private final ApplicationEventPublisher eventPublisher;

  // 本地快照，避免 getRouteDefinitions 时反复解析配置
  private final Map<String, RouteDefinition> routeCache = new ConcurrentHashMap<>();

  @PostConstruct
  public void init() throws Exception {
    refreshRouteCache(loadConfigText());
    nacosConfigManager
        .getConfigService()
        .addListener(
            DATA_ID,
            GROUP,
            new Listener() {
              @Override
              public Executor getExecutor() {
                return null;
              }

              @Override
              public void receiveConfigInfo(String configInfo) {
                try {
                  refreshRouteCache(configInfo);
                  eventPublisher.publishEvent(new RefreshRoutesEvent(this));
                  log.info("Gateway routes refreshed from Nacos, routeCount={}", routeCache.size());
                } catch (Exception ex) {
                  log.error("Refresh gateway routes failed, keep old cache", ex);
                }
              }
            });
  }

  @Override
  public Flux<RouteDefinition> getRouteDefinitions() {
    return Flux.fromIterable(routeCache.values());
  }

  @Override
  public Mono<Void> save(Mono<RouteDefinition> route) {
    return Mono.error(new UnsupportedOperationException("Route write is managed by Nacos"));
  }

  @Override
  public Mono<Void> delete(Mono<String> routeId) {
    return Mono.error(new UnsupportedOperationException("Route delete is managed by Nacos"));
  }

  private String loadConfigText() throws Exception {
    String configText =
        nacosConfigManager
            .getConfigService()
            .getConfig(DATA_ID, GROUP, nacosConfigManager.getNacosConfigProperties().getTimeout());
    if (StringUtils.isBlank(configText)) {
      throw new RuntimeException("无法加载配置文件：dataId=" + DATA_ID + ", group=" + GROUP);
    }
    return configText;
  }

  private void refreshRouteCache(String configText) throws Exception {
    log.info("刷新路由缓存:{}", configText);
    List<GatewayRouteConfig> configs =
        objectMapper.readValue(configText, new TypeReference<List<GatewayRouteConfig>>() {});

    routeConfigValidator.validate(configs);

    Map<String, RouteDefinition> newCache = new ConcurrentHashMap<>();
    for (GatewayRouteConfig config : configs) {
      if (Boolean.FALSE.equals(config.getEnabled())) {
        continue;
      }
      RouteDefinition definition = routeDefinitionConverter.convert(config);
      newCache.put(definition.getId(), definition);
    }

    routeCache.clear();
    routeCache.putAll(newCache);
  }
}
