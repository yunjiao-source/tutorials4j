package tutorials4j.framework.web.http;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.web.core.properties.WebHttpProperties;

import java.io.IOException;

/**
 * 缓存请求体内容的过滤器。
 *
 * <p>该过滤器用于将原始的 {@link HttpServletRequest} 包装为 {@link CachedHttpServletRequestWrapper}，
 * 从而支持后续对请求体的多次读取。包装前会检查请求体的 Content-Length 是否超过配置的最大允许长度，
 * 若超过则放弃包装并记录警告日志，此时原始请求体将不可重复读取。
 *
 * <p>过滤器会避免重复包装同一个请求（即如果请求已经是 {@code CachedHttpServletRequestWrapper} 实例，
 * 则直接放行）。
 *
 * @author Yun Jiao
 * @see CachedHttpServletRequestWrapper
 * @see WebHttpProperties.CachedRequestBody
 */
@Slf4j
@RequiredArgsConstructor
public class CachedRequestBodyFilter  implements Filter {
    private final WebHttpProperties.CachedRequestBody crb;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest) {
            // 避免重复包装
            if (!(httpRequest instanceof CachedHttpServletRequestWrapper)) {
                // 检查 Content-Length
                int length = httpRequest.getContentLength();
                if (length > crb.getMaxContentLength().toBytes()) {
                    log.warn("请求体长度(Content Length)超过最大值[字节]：{} 。放弃包装，请求体将不可重复读取", crb.getMaxContentLength().toBytes());
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
