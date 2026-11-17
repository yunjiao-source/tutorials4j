package tutorials4j.toolkit.satoken.strategy;

import cn.dev33.satoken.oauth2.exception.SaOAuth2AccessTokenException;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import io.micrometer.tracing.Tracer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class OAuth2FilterErrorStrategy extends CommonFilterErrorStrategy {

  public OAuth2FilterErrorStrategy(Tracer tracer) {
    super(tracer);
  }

  @Override
  protected ProblemDetail handleThrowable(Throwable t) {
    if (t instanceof SaOAuth2Exception saOAuth2Exception) {
      if (t instanceof SaOAuth2AccessTokenException) {
        return createProblemDetail(saOAuth2Exception, HttpStatus.UNAUTHORIZED, "登录已失效，请重新登录");
      }
    }
    return super.handleThrowable(t);
  }
}
