package tutorials4j.framework.web.core.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 缓存请求体过滤器
 *
 * @author Yun Jiao
 */
@Slf4j
public class CachedRequestBodyFilter  implements Filter {
    private final CachedRequestBodyProperties properties;

    public CachedRequestBodyFilter(CachedRequestBodyProperties properties) {
        this.properties = properties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest) {
            // 避免重复包装
            if (!(httpRequest instanceof CachedHttpServletRequestWrapper)) {
                // 检查 Content-Length
                int length = httpRequest.getContentLength();
                if (length > properties.getMaxContentLength().toBytes()) {
                    log.warn("请求体长度(Content Length)超过最大值[字节]：{} 。放弃包装，请求体将不可重复读取", properties.getMaxContentLength().toBytes());
                } else {
                    httpRequest = new CachedHttpServletRequestWrapper(httpRequest);
                }
            }
            chain.doFilter(httpRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 可选初始化逻辑
    }

    @Override
    public void destroy() {
        // 可选销毁逻辑
    }
}
