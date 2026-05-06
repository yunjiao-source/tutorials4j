package tutorials4j.framework.web.mvc.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.web.mvc.support.XssHttpServletRequestWrapper;

import java.io.IOException;

/**
 * XSS 攻击防护过滤器。
 * <p>
 * 该过滤器将原始的 {@link HttpServletRequest} 包装为 {@link XssHttpServletRequestWrapper}，
 * 从而对请求参数、请求头等数据进行 AntiSamy 清洗，防止跨站脚本攻击。
 * </p>
 *
 * @author Yun Jiao
 * @see XssHttpServletRequestWrapper
 */
@Slf4j
public class XssHttpServletFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(request);
        if (log.isDebugEnabled()) {
            log.debug("Tutorials4j - Web |- Xss攻击过滤器：{}", request.getRequestURI());
        }
        filterChain.doFilter(xssRequest, servletResponse);
    }
}
