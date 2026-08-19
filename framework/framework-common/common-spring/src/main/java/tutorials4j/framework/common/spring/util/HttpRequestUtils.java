package tutorials4j.framework.common.spring.util;

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
 * HTTP 请求工具类，提供请求/响应的 Debug 日志输出与字符集解析能力，常用于 RestTemplate 拦截器。
 *
 * @author Yun Jiao
 */
@Slf4j
public class HttpRequestUtils {

  /**
   * 以 Debug 级别输出 HTTP 请求的请求行、请求头与请求体。
   *
   * @param request HTTP 请求
   * @param body 请求体字节数组
   */
  public static void requestLogger(HttpRequest request, byte[] body) {
    if (!log.isDebugEnabled()) {
      return;
    }

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
    log.debug(sb.toString());
  }

  /**
   * 以 Debug 级别输出 HTTP 响应的状态码与响应头。
   *
   * @param response 客户端 HTTP 响应
   */
  public static void responseLogger(ClientHttpResponse response) {
    if (!log.isDebugEnabled()) {
      return;
    }

    try {
      StringBuilder sb = new StringBuilder("\n");
      sb.append("[ClientHttpResponse]响应: ").append(response.getStatusCode()).append("\n");
      sb.append("响应头列表: \n");
      response.getHeaders().forEach((k, v) -> headerLogger(sb, k, v));
      log.debug(sb.toString());
    } catch (IOException e) {
      log.error("获取响应异常", e);
    }
  }

  /**
   * 从响应/请求头中解析字符集，未指定时返回默认字符集。
   *
   * @param headers HTTP 头集合
   * @return 解析得到的字符集
   */
  public static Charset getCharset(HttpHeaders headers) {
    return Optional.ofNullable(headers.getContentType())
        .map(MediaType::getCharset)
        .orElse(DefaultConsts.DEFAULT_CHARSET);
  }

  private static void headerLogger(StringBuilder logBuilder, String name, List<String> values) {
    values.forEach(value -> logBuilder.append(name).append("=").append(value).append("\n"));
  }
}
