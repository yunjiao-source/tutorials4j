package tutorials4j.framework.data.core.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import tutorials4j.framework.common.core.HttpHeaderUtils;

/**
 * 租户拦截器
 *
 * @author Yun Jiao
 */
public class TenantHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头中获取租户标识（也可以从Token、Session中解析）
        String tenant = HttpHeaderUtils.getTenant(request);
        if (StringUtils.hasText(tenant)) {
            TenantContextHolder.set(tenant);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        TenantContextHolder.clear();  // 请求结束后清除，防止内存泄漏
    }
}
