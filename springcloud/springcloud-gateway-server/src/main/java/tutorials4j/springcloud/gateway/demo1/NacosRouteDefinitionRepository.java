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
/**
 * 基于 Nacos 配置中心的动态路由仓库：启动时从 Nacos 加载路由配置，并监听配置变更实时刷新网关路由。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NacosRouteDefinitionRepository implements RouteDefinitionRepository {

  /** Nacos 中路由配置的 dataId。 */
  private static final String DATA_ID = "gateway-routes.json";

  /** Nacos 中路由配置所属的 group。 */
  private static final String GROUP = "GATEWAY_GROUP";

  private final NacosConfigManager nacosConfigManager;
  private final ObjectMapper objectMapper;
  private final RouteDefinitionConverter routeDefinitionConverter;
  private final RouteConfigValidator routeConfigValidator;
  private final ApplicationEventPublisher eventPublisher;

  // 本地快照，避免 getRouteDefinitions 时反复解析配置
  private final Map<String, RouteDefinition> routeCache = new ConcurrentHashMap<>();

  /**
   * 初始化：加载 Nacos 中的路由配置并注册配置监听器，配置变更时刷新本地缓存并发布路由刷新事件。
   *
   * @throws Exception 加载或解析配置失败时抛出
   */
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

  /** 返回本地缓存中的所有路由定义。 */
  @Override
  public Flux<RouteDefinition> getRouteDefinitions() {
    return Flux.fromIterable(routeCache.values());
  }

  /** 不支持通过代码写入路由，路由的写入统一由 Nacos 配置管理。 */
  @Override
  public Mono<Void> save(Mono<RouteDefinition> route) {
    return Mono.error(new UnsupportedOperationException("Route write is managed by Nacos"));
  }

  /** 不支持通过代码删除路由，路由的删除统一由 Nacos 配置管理。 */
  @Override
  public Mono<Void> delete(Mono<String> routeId) {
    return Mono.error(new UnsupportedOperationException("Route delete is managed by Nacos"));
  }

  /**
   * 从 Nacos 拉取路由配置文件内容。
   *
   * @return 路由配置的 JSON 文本
   * @throws Exception 配置为空或拉取失败时抛出
   */
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

  /**
   * 解析路由配置文本，校验后转换为 RouteDefinition 并整体替换本地缓存。
   *
   * @param configText 路由配置的 JSON 文本
   * @throws Exception 配置解析失败时抛出
   */
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
