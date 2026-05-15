package tutorials4j.framework.common.core.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import tutorials4j.framework.common.core.DefaultConsts;

/**
 * HTTP请求工具
 *
 * @author Yun Jiao
 */
@Slf4j
public class HttpRequestUtils {

  public static void requestLogger(HttpRequest request, byte[] body) {
    StringBuilder sb = new StringBuilder("\n");
    sb.append("[HttpRequest]请求: ")
        .append(request.getMethod())
        .append(" ")
        .append(request.getURI())
        .append("\n");
    sb.append("请求头列表: \n");
    request.getHeaders().forEach((k, v) -> headerLogger(sb, k, v));

    sb.append("请求体:\n");
    if (body.length > 0) {
      sb.append(new String(body, getCharset(request.getHeaders()))).append("\n");
    }
    log.info(sb.toString());
  }

  public static void responseLogger(ClientHttpResponse response) {
    try {
      StringBuilder sb = new StringBuilder("\n");
      sb.append("[ClientHttpResponse]响应: ").append(response.getStatusCode()).append("\n");
      sb.append("响应头列表: \n");
      response.getHeaders().forEach((k, v) -> headerLogger(sb, k, v));
      log.info(sb.toString());
    } catch (IOException e) {
      log.error("获取响应状态异常", e);
    }
  }

  public static Charset getCharset(HttpHeaders headers) {
    return Optional.ofNullable(headers.getContentType())
        .map(MediaType::getCharset)
        .orElse(DefaultConsts.DEFAULT_CHARSET);
  }

  private static void headerLogger(StringBuilder logBuilder, String name, List<String> values) {
    values.forEach(value -> logBuilder.append(name).append("=").append(value).append("\n"));
  }
}
