package tutorials4j.framework.web.mvc.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
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
public class XssRequestFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(request);
        if (log.isDebugEnabled()) {
            log.debug("Tutorials4j - Web |- Xss攻击过滤器：{}", request.getRequestURI());
        }
        filterChain.doFilter(xssRequest, response);
    }
}
