package tutorials4j.framework.web.client.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import tutorials4j.framework.web.core.WebFrameworkException;

import java.util.List;

/**
 * 响应式客户端工具类
 *
 * @author Yun Jiao
 */
@Slf4j
public class ReactiveClientUtils {
    /**
     * 创建一个用于捕获异常并转换为框架自定义异常的 {@link ExchangeFilterFunction}。
     * <p>
     * 该过滤器会对响应状态码进行检查，如果状态码大于 300（即非成功状态码，2xx 除外），
     * 则读取响应体内容，并将其作为异常消息，抛出 {@link WebFrameworkException}；
     * 否则直接返回原始响应。
     * </p>
     * <p>
     * 注意：由于响应体只能被消费一次，该过滤器会消耗响应体，后续的处理器将无法再次读取响应体。
     * </p>
     *
     * @return 用于异常捕获和转换的 {@link ExchangeFilterFunction} 实例
     */
    public static ExchangeFilterFunction ofCatchExcepitonLogger() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            HttpStatusCode status = response.statusCode();
            if (status.value() > 300) {
                return response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new WebFrameworkException("接口调用异常: " + body)));
            }

            return Mono.just(response);
        });
    }

    /**
     * 创建一个用于记录客户端请求日志的 {@link ExchangeFilterFunction}。
     * <p>
     * 该过滤器在请求发送前，调用 {@link #requestLoggerDetails(ClientRequest)} 方法，
     * 将请求的方法、URL 以及所有请求头信息输出到日志中。
     * </p>
     *
     * @return 请求日志记录的 {@link ExchangeFilterFunction} 实例
     * @see #requestLoggerDetails(ClientRequest)
     */
    public static ExchangeFilterFunction ofClientRequestLogger() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            requestLoggerDetails(clientRequest);
            return Mono.just(clientRequest);
        });
    }

    /**
     * 创建一个用于记录客户端响应日志的 {@link ExchangeFilterFunction}。
     * <p>
     * 该过滤器在收到响应后，调用 {@link #responseLoggerDetails(ClientResponse)} 方法，
     * 将响应的状态码以及所有响应头信息输出到日志中。
     * </p>
     *
     * @return 响应日志记录的 {@link ExchangeFilterFunction} 实例
     * @see #responseLoggerDetails(ClientResponse)
     */
    public static ExchangeFilterFunction ofClientResponseLogger() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            responseLoggerDetails(clientResponse);
            return Mono.just(clientResponse);
        });
    }

    /**
     * 记录客户端响应的详细信息。
     * <p>
     * 该方法会从 {@link ClientResponse} 中提取状态码和所有响应头，
     * 拼接成格式化的字符串后通过日志输出（INFO 级别）。
     * </p>
     * <p>
     * 输出格式示例：
     * <pre>
     * [ClientResponse]响应: 200 OK
     * 响应头列表:
     * Content-Type=application/json
     * Content-Length=123
     * </pre>
     * </p>
     *
     * @param response 需要记录日志的客户端响应对象，不能为 {@code null}
     */
    public static void responseLoggerDetails(ClientResponse response) {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("[ClientResponse]响应: ")
                .append(response.statusCode())
                .append("\n");
        sb.append("响应头列表: \n");
        response.headers().asHttpHeaders().forEach((k, v) -> headerLogger(sb, k, v));
        log.info(sb.toString());
    }

    /**
     * 记录客户端请求的详细信息。
     * <p>
     * 该方法会从 {@link ClientRequest} 中提取请求方法、URL 以及所有请求头，
     * 拼接成格式化的字符串后通过日志输出（INFO 级别）。
     * </p>
     * <p>
     * 输出格式示例：
     * <pre>
     * [ClientRequest]请求: GET https://api.example.com/data
     * 请求头列表:
     * Accept=application/json
     * Authorization=Bearer xxxx
     * </pre>
     * </p>
     *
     * @param request 需要记录日志的客户端请求对象，不能为 {@code null}
     */
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

    /**
     * 辅助方法：将单个请求或响应的头信息追加到日志字符串构建器中。
     * <p>
     * 对于给定的头名称和对应的值列表，该方法会将每个值以“名称=值”的格式追加到 {@link StringBuilder} 中，
     * 每个头值独占一行。
     * </p>
     *
     * @param logBuilder 用于拼接日志内容的 {@link StringBuilder} 实例
     * @param name       头名称
     * @param values     与头名称对应的值列表
     */
    private static void headerLogger(StringBuilder logBuilder, String name, List<String> values) {
        values.forEach(value -> logBuilder.append(name).append("=").append(value).append("\n"));
    }
}
