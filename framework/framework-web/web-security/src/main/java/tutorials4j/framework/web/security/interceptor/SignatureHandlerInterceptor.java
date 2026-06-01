package tutorials4j.framework.web.security.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.spring.util.HeaderUtils;
import tutorials4j.framework.web.core.exception.SignatureException;
import tutorials4j.framework.web.security.annotation.RequiredSignature;
import tutorials4j.framework.web.security.cache.SignatureCacheTemplate;
import tutorials4j.framework.web.security.signature.SignatureKeyRepository;
import tutorials4j.framework.web.security.signature.SignatureUtils;

/**
 * 签名校验拦截器。
 *
 * <p>用于对标注了 {@link RequiredSignature} 且 required=true 的接口进行签名验证，
 * 包括参数完整性检查、时间窗有效期验证、防重放攻击（Nonce）以及签名一致性校验。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class SignatureHandlerInterceptor implements HandlerInterceptor {
  private final SignatureCacheTemplate signatureCacheTemplate;
  private final SignatureKeyRepository signatureKeyRepository;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    if (log.isDebugEnabled()) {
      log.debug("[WEB-MVC] 签名拦截器：{}", request.getRequestURI());
    }

    Method method = null;
    if (handler instanceof HandlerMethod handlerMethod) {
      method = handlerMethod.getMethod();
    }

    if (method == null) {
      return true;
    }

    HandlerMethod handlerMethod = (HandlerMethod) handler;
    RequiredSignature annotation = handlerMethod.getMethodAnnotation(RequiredSignature.class);
    if (annotation == null || !annotation.required()) {
      return true;
    }

    String appKey = HeaderUtils.getHeader(request, DefaultConsts.HTTP_HEADER_SIGNATURE_APP_KEY);
    String timestamp =
        HeaderUtils.getHeader(request, DefaultConsts.HTTP_HEADER_SIGNATURE_TIMESTAMP);
    String nonce = HeaderUtils.getHeader(request, DefaultConsts.HTTP_HEADER_SIGNATURE_NONCE);
    String signature = HeaderUtils.getHeader(request, DefaultConsts.HTTP_HEADER_SIGNATURE);

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
    if (annotation.checkNonce() && !signatureCacheTemplate.setIfAbsent(nonce)) {
      throw new SignatureException("重复的请求");
    }

    String appSecret = signatureKeyRepository.getSecretKey(appKey);
    if (StringUtils.isBlank(appSecret)) {
      throw new SignatureException("未找到签名KEY");
    }

    // 4. 签名验证
    String requestBody = getRequestBody(request);
    boolean valid =
        SignatureUtils.verify(
            appKey,
            appSecret,
            timestamp,
            nonce,
            request.getMethod(),
            request.getRequestURI(),
            requestBody,
            signature);

    if (!valid) {
      throw new SignatureException("签名验证失败");
    }

    return true;
  }

  /**
   * 获取 HTTP 请求的 Body 内容。
   *
   * @param request HTTP 请求对象
   * @return 请求体字符串
   * @throws IOException 读取请求体时发生 I/O 错误
   */
  private String getRequestBody(HttpServletRequest request) throws IOException {
    String charset = request.getCharacterEncoding();
    if (charset == null) {
      charset = StandardCharsets.UTF_8.name();
    }
    byte[] bytes = IOUtils.toByteArray(request.getReader(), charset);
    return new String(bytes);
  }
}
