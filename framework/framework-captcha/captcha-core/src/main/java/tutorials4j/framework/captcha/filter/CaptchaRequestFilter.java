package tutorials4j.framework.captcha.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tutorials4j.framework.captcha.CaptchaServiceFactory;
import tutorials4j.framework.captcha.exception.CaptchaException;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.spring.util.HeaderUtils;
import tutorials4j.framework.common.spring.web.RemoveHeaderRequestWrapper;

/**
 * 验证码请求过滤器。
 *
 * <p>该过滤器在每个请求中拦截并校验请求头中的验证码参数（key、category、code）。 若参数不完整或验证失败，则抛出 {@link CaptchaException}
 * 异常；若校验通过， 则移除验证码相关的请求头后继续执行过滤器链。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class CaptchaRequestFilter extends OncePerRequestFilter {
  private final CaptchaServiceFactory captchaServiceFactory;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String key = HeaderUtils.getHeader(request, DefaultConsts.HTTP_HEADER_CAPTCHA_KEY);
    String category = HeaderUtils.getHeader(request, DefaultConsts.HTTP_HEADER_CAPTCHA_CATEGORY);
    String code = HeaderUtils.getHeader(request, DefaultConsts.HTTP_HEADER_CAPTCHA_CODE);

    // 1. 参数校验
    if (StringUtils.isAnyBlank(key, category, code)) {
      throw new CaptchaException("验证码参数不完整");
    }

    if (!captchaServiceFactory.findService(category).verify(key, code)) {
      throw new CaptchaException("验证码校验失败");
    }

    // 删除验证码请求头
    RemoveHeaderRequestWrapper wrapper =
        new RemoveHeaderRequestWrapper(request, DefaultConsts.HTTP_HEADER_CAPTCHA);
    filterChain.doFilter(wrapper, response);
  }
}
