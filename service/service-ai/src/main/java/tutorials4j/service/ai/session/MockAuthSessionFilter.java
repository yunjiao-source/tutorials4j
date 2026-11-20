package tutorials4j.service.ai.session;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class MockAuthSessionFilter extends OncePerRequestFilter {

  public static final String HEADER = "X-User";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    if (log.isDebugEnabled()) {
      log.debug("从请求头获取登录信息");
    }

    boolean isContextSet = false;
    try {
      String raw = request.getHeader(HEADER);
      if (StringUtils.hasText(raw)) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(raw, null));
        SecurityContextHolder.setContext(context);
        isContextSet = true;
      }
      chain.doFilter(request, response);
    } finally {
      if (isContextSet) {
        SecurityContextHolder.clearContext();
      }
    }
  }

  @Override
  protected boolean shouldNotFilterAsyncDispatch() {
    return false;
  }

  @Override
  protected boolean shouldNotFilterErrorDispatch() {
    return false;
  }
}
