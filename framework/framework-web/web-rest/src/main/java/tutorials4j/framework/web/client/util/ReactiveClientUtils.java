package tutorials4j.framework.web.client.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import tutorials4j.framework.web.core.WebClientFrameworkException;

import java.util.List;

/**
 * 响应式客户端工具
 *
 * @author Yun Jiao
 */
@Slf4j
public class ReactiveClientUtils {
    public static ExchangeFilterFunction ofCatchExcepitonLogger() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            HttpStatusCode status = response.statusCode();
            if (status.value() > 300) {
                return response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new WebClientFrameworkException("接口调用异常: " + body)));
            }

            return Mono.just(response);
        });
    }

    public static ExchangeFilterFunction ofClientRequestLogger() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            requestLoggerDetails(clientRequest);
            return Mono.just(clientRequest);
        });
    }

    public static ExchangeFilterFunction ofClientResponseLogger() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            responseLoggerDetails(clientResponse);
            return Mono.just(clientResponse);
        });
    }

    public static void responseLoggerDetails(ClientResponse response) {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("[ClientResponse]响应: ")
                .append(response.statusCode())
                .append("\n");
        sb.append("响应头列表: \n");
        response.headers().asHttpHeaders().forEach((k, v) -> headerLogger(sb, k, v));
        log.info(sb.toString());
    }

    public static void requestLoggerDetails(ClientRequest request) {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("[ClientRequest]请求: ")
                .append(request.method())
                .append(" ")
                .append(request.url())
                .append("\n");
        sb.append("请求头列表: \n");
        request.headers().forEach((k, v) -> headerLogger(sb, k, v));
        log.info(sb.toString());

    }

    private static void headerLogger(StringBuilder logBuilder, String name, List<String> values) {
        values.forEach(value -> logBuilder.append(name).append("=").append(value).append("\n"));
    }
}
