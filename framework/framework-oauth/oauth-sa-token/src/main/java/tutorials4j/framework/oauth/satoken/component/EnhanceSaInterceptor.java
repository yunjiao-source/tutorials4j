package tutorials4j.framework.oauth.satoken.component;

import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import org.springframework.web.method.HandlerMethod;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class EnhanceSaInterceptor extends SaInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    try {

      // 这里必须确保 handler 是 HandlerMethod 类型时，才能进行注解鉴权
      if (isAnnotation && handler instanceof HandlerMethod) {
        Method method = ((HandlerMethod) handler).getMethod();
        SaAnnotationStrategy.instance.checkMethodAnnotation.accept(method);
      }

      // Auth 校验
      auth.run(handler);

    } catch (StopMatchException e) {
      // StopMatchException 异常代表：停止匹配，进入Controller
    }

    // 通过验证
    return true;
  }
}
