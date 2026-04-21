package tutorials4j.framework.common.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import tutorials4j.framework.common.lang.DefaultConsts;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

/**
 * HTTP请求工具
 *
 * @author Yun Jiao
 */
@Slf4j
public class HttpRequestUtils {
    /**
     * 请求日志记录
     *
     * @param request 请求
     * @param body 体
     */
    public static void requestLogging(HttpRequest request, byte[] body) {
        log.info("请求: {} {}", request.getMethod(), request.getURI());
        log.info("--- 请求头列表: ---");
        request.getHeaders().forEach(HttpRequestUtils::logHeader);

        log.info("--- 请求体: ---");
        log.info(new String(body, getCharset(request.getHeaders())));
    }

    /**
     * 响应日志记录，不包括'响应体'
     * @param response 响应
     */
    public static void responseLogging(ClientHttpResponse response) {
        try {
            log.info("响应: {}", response.getStatusCode());
        } catch (IOException e) {
            log.error("获取响应状态异常", e);
        }
        log.info("--- 响应头列表: ---");
        response.getHeaders().forEach(HttpRequestUtils::logHeader);
    }

    public static Charset getCharset(HttpHeaders headers)
    {
        return Optional.of(headers.getContentType())
                .map(MediaType::getCharset)
                .orElse(DefaultConsts.DEFAULT_CHARSET);
    }

    private static void logHeader(String name, List<String> values) {
        values.forEach(value -> log.info("{}={}", name, value));
    }
}
