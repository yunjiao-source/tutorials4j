package tutorials4j.framework.web.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.util.List;

/**
 * 响应式客户端工具
 *
 * @author Yun Jiao
 */
@Slf4j
public class ReactiveClientUtils {
    /**
     * 请求日志记录
     *
     * @param request 请求
     */
    public static void requestLogging(ClientRequest request) {
        log.info("请求: {} {}", request.method(), request.url());
        log.info("\t 请求头列表:");
        request.headers().forEach(ReactiveClientUtils::logHeader);

    }

    /**
     * 响应日志记录，不包括'响应体'
     * @param response 响应
     */
    public static void responseLogging(ClientResponse response) {
        log.info("响应: {}", response.statusCode());
        log.info("\t 响应头列表:");
        response.headers().asHttpHeaders().forEach(ReactiveClientUtils::logHeader);
    }

    private static void logHeader(String name, List<String> values) {
        values.forEach(value -> log.info("\t\t {}={}", name, value));
    }
}
