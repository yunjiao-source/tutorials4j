package tutorials4j.framework.web.google.auth.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.spring.util.SecurityUtils;
import tutorials4j.framework.common.spring.web.RemoveHeaderRequestWrapper;
import tutorials4j.framework.web.core.exception.WebFrameworkException;
import tutorials4j.framework.web.google.auth.GoogleAuthService;

/**
 * TOTP 验证请求过滤器。
 *
 * <p>该过滤器会拦截请求，从请求头中获取用户名和 TOTP 验证码，执行校验。 校验失败时抛出 {@link WebFrameworkException}；校验通过后移除验证码相关的请求头，
 * 避免后续处理再次读取敏感信息。
 *
 * <p>用户名的获取顺序：
 *
 * <ol>
 *   <li>首先尝试读取请求头 {@link DefaultConsts#HTTP_HEADER_GOOGLE_AUTH_USERNAME}
 *   <li>若为空，则调用 {@link SecurityUtils#getAccount()} 从当前安全上下文中获取
 * </ol>
 *
 * @author Yun Jiao
 * @see GoogleAuthService
 */
@Slf4j
@RequiredArgsConstructor
public class GoogleAuthRequestFilter extends OncePerRequestFilter {

  private final GoogleAuthService googleAuthService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String userName = request.getHeader(DefaultConsts.HTTP_HEADER_GOOGLE_AUTH_USERNAME);
    if (StringUtils.isBlank(userName)) {
      userName = SecurityUtils.getAccount();
    }
    String code = request.getHeader(DefaultConsts.HTTP_HEADER_GOOGLE_AUTH_CODE);

    // 1. 参数校验
    if (StringUtils.isAnyBlank(userName, code)) {
      throw new WebFrameworkException("Google Auth 参数不完整");
    }

    if (!googleAuthService.verifyByUserName(userName, Integer.parseInt(code))) {
      throw new WebFrameworkException("Google Auth 校验失败");
    }

    // 删除验证码请求头
    if (request instanceof RemoveHeaderRequestWrapper wrapper) {
      wrapper.getHeadersToRemove().addAll(Arrays.asList(DefaultConsts.HTTP_HEADER_GOOGLE_AUTH));
      filterChain.doFilter(wrapper, response);
    } else {
      RemoveHeaderRequestWrapper wrapper =
          new RemoveHeaderRequestWrapper(request, DefaultConsts.HTTP_HEADER_GOOGLE_AUTH);
      filterChain.doFilter(wrapper, response);
    }
  }
}
