package tutorials4j.framework.core.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 将原始 request 包装为 {@link CachedBodyRequest}
 *
 * @author Yun Jiao
 */
@Slf4j
@Getter
@Setter
public class CachedBodyFilter implements Filter {
    private long maxContentLength;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest) {
            // 避免重复包装
            if (!(httpRequest instanceof CachedBodyRequest)) {
                // 检查 Content-Length
                int length = httpRequest.getContentLength();
                if (length > maxContentLength) {
                    log.warn("请求体长度(Content Length)超过最大值[字节]：{} 。放弃包装，请求体将不可重复读取", maxContentLength);
                } else {
                    httpRequest = new CachedBodyRequest(httpRequest);
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

