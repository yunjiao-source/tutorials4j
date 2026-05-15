package tutorials4j.framework.web.mvc.filter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import tutorials4j.framework.web.core.properties.HttpProperties;

/**
 * 默认的请求日志过滤器，扩展自 Spring 的 {@link CommonsRequestLoggingFilter}。
 *
 * <p>根据 {@link HttpProperties.RequestLoggingOptions} 配置以下行为：
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
 * @see HttpProperties.RequestLoggingOptions
 */
@RequiredArgsConstructor
public class DefaultCommonsRequestLoggingFilter extends CommonsRequestLoggingFilter {
  private final HttpProperties.RequestLoggingOptions options;

  @Override
  protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
    String newsuffix = suffix;
    if (options.isIncludeTimestamp()) {
      newsuffix = ", timestamp=" + System.currentTimeMillis() + suffix;
    }
    return super.createMessage(request, prefix, newsuffix);
  }

  @Override
  protected boolean shouldLog(HttpServletRequest request) {
    return true;
  }

  /**
   * 初始化过滤器配置。
   *
   * <p>将 {@link HttpProperties.RequestLoggingOptions} 中的各项配置应用到当前过滤器实例。
   */
  public void init() {
    setIncludeClientInfo(options.isIncludeClientInfo());
    setIncludeClientInfo(options.isIncludeClientInfo());
    setIncludeHeaders(options.isIncludeHeaders());
    setIncludePayload(options.isIncludePayload());
    setMaxPayloadLength(options.getMaxPayloadLength());
    setBeforeMessagePrefix(options.getBeforeMessagePrefix());
    setBeforeMessageSuffix(options.getBeforeMessageSuffix());
    setAfterMessagePrefix(options.getAfterMessagePrefix());
    setAfterMessageSuffix(options.getAfterMessageSuffix());
  }
}
