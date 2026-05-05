package tutorials4j.framework.web.rest.mdc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import tutorials4j.framework.common.core.DefaultConsts;

import java.util.Map;
import java.util.stream.Collectors;


/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class TraceExchangeFilterFunction implements ExchangeFilterFunction {

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        // 从当前线程 MDC 获取快照
        Map<String, String> currentMdc = MDC.getCopyOfContextMap() != null ? MDC.getCopyOfContextMap() : Map.of();

        // 过滤出需要传播的键值对
        Map<String, String> toPropagate = currentMdc.entrySet().stream()
                .filter(entry -> DefaultConsts.HTTP_MDC_KEYS.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // 将 MDC 值添加到请求头
        ClientRequest filteredRequest = ClientRequest.from(request)
                .headers(headers -> toPropagate.forEach(headers::set))
                .build();

        if (log.isDebugEnabled()) {
            log.debug("Tutorials4j - Web |- 跟踪信息WebFlux过滤器：{}", request.url());
        }
        // 继续执行请求，并将当前 MDC 快照存入 Reactor Context
        return next.exchange(filteredRequest)
                .contextWrite(Context.of(DefaultConsts.MDC_CONTEXT_KEY, toPropagate))
                .doOnEach(signal -> {
                    // 在收到响应后，可选择从响应头恢复上下游的跟踪信息
                    if (!signal.isOnNext()) {
                        return;
                    }

                    ClientResponse response = signal.get();
                    // 响应头提取新的 traceId 并更新 MDC
                    if (response != null) {
                        ClientResponse.Headers headers = response.headers();
                        for (String headerName : DefaultConsts.HTTP_MDC_KEYS) {
                            String headerValue = CollectionUtils.firstElement(headers.header(headerName));
                            if (StringUtils.isNotBlank(headerValue)) {
                                MDC.put(headerName, headerValue);
                            }
                        }
                    }
                });
    }
}
