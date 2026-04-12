package tutorials4j.springboot3;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 签名验证拦截器
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class SignatureInterceptor implements HandlerInterceptor {
    private final NonceService nonceService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireSignature annotation = handlerMethod.getMethodAnnotation(RequireSignature.class);

        if (annotation == null || !annotation.required()) {
            return true;
        }

        // 验证签名
        try {
            String appKey = request.getHeader("X-App-Key");
            String timestamp = request.getHeader("X-Timestamp");
            String nonce = request.getHeader("X-Nonce");
            String signature = request.getHeader("X-Signature");

            // 1. 参数校验
            if (StringUtils.isAnyBlank(appKey, timestamp, nonce, signature)) {
                throw new SignatureException("签名参数不完整");
            }

            // 2. 时间戳验证
            long requestTime = Long.parseLong(timestamp);
            long currentTime = System.currentTimeMillis();
            if (Math.abs(currentTime - requestTime) > annotation.timeWindow() * 1000) {
                throw new SignatureException("请求已过期");
            }

            // 3. Nonce 验证（防重放）
            if (annotation.checkNonce() && nonceService.exists(nonce)) {
                throw new SignatureException("重复的请求");
            }

            // 4. 签名验证
            String requestBody = getRequestBody(request);
            boolean valid = SignatureUtils.verify(appKey, timestamp, nonce,
                    request.getMethod(),
                    request.getRequestURI(),
                    requestBody,
                    signature);

            if (!valid) {
                throw new SignatureException("签名验证失败");
            }

            // 5. 记录 nonce
            if (annotation.checkNonce()) {
                nonceService.save(nonce, annotation.timeWindow());
            }

            return true;
        } catch (SignatureException e) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"" + e.getMessage() + "\"}");
            return false;
        }
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        String charset = request.getCharacterEncoding();
        if (charset == null) {
            charset = StandardCharsets.UTF_8.name();
        }
        byte[] bytes = IOUtils.toByteArray(request.getReader(), charset);
        return new String(bytes);
    }
}
