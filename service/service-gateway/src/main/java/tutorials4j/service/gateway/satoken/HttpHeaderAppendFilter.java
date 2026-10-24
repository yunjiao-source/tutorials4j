package tutorials4j.service.gateway.satoken;

import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tutorials4j.toolkit.core.constant.DefaultConsts;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Component
public class HttpHeaderAppendFilter implements GlobalFilter {
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    try {
      SaReactorSyncHolder.setContext(exchange);
      ServerHttpRequest newRequest =
          exchange
              .getRequest()
              .mutate()
              .headers(
                  headers -> {
                    headers.add(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
                    headers.add(DefaultConsts.HTTP_HEADER_LOGIN_ID, StpUtil.getLoginId(""));
                  })
              .build();
      ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();

      return chain.filter(newExchange);
    } finally {
      SaReactorSyncHolder.clearContext();
    }
  }
}
