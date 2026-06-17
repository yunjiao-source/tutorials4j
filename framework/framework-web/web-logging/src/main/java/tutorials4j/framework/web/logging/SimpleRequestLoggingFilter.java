package tutorials4j.framework.web.logging;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import tutorials4j.framework.web.logging.properties.RequestLoggingWebProperties.RequestOptions;

/**
 * 默认的请求日志过滤器，扩展自 Spring 的 {@link CommonsRequestLoggingFilter}。
 *
 * <p>根据 {@link RequestOptions} 配置以下行为：
 *
 * <ul>
 *   <li>是否包含客户端信息（IP、会话ID等）
 *   <li>是否包含请求头
 *   <li>是否包含请求体 payload
 *   <li>payload 最大记录长度
 *   <li>请求前/后的消息前缀和后缀
 *   <li>是否添加时间戳到日志消息中
 * </ul>
 *
 * 重写 {@link #shouldLog(HttpServletRequest)} 始终返回 {@code true}，确保所有请求均被记录。
 *
 * @author Yun Jiao
 * @see CommonsRequestLoggingFilter
 * @see RequestOptions
 */
public class SimpleRequestLoggingFilter extends CommonsRequestLoggingFilter {
  @Override
  protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
    return super.createMessage(request, prefix, suffix);
  }

  @Override
  protected boolean shouldLog(HttpServletRequest request) {
    return true;
  }

  /**
   * 初始化过滤器配置。
   *
   * <p>将 {@link RequestOptions} 中的各项配置应用到当前过滤器实例。
   */
  public void init(RequestOptions requestOptions) {
    setIncludeClientInfo(requestOptions.isIncludeClientInfo());
    setIncludeClientInfo(requestOptions.isIncludeClientInfo());
    setIncludeHeaders(requestOptions.isIncludeHeaders());
    setIncludePayload(requestOptions.isIncludePayload());
    setMaxPayloadLength(requestOptions.getMaxPayloadLength());
    setBeforeMessagePrefix(requestOptions.getBeforeMessagePrefix());
    setBeforeMessageSuffix(requestOptions.getBeforeMessageSuffix());
    setAfterMessagePrefix(requestOptions.getAfterMessagePrefix());
    setAfterMessageSuffix(requestOptions.getAfterMessageSuffix());
  }
}
