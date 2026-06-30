package tutorials4j.framework.crypto.web.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.spring.jackson.Jackson2Utils;
import tutorials4j.framework.common.spring.util.HeaderUtils;
import tutorials4j.framework.crypto.core.annotation.Crypto;
import tutorials4j.framework.crypto.core.cache.CryptoProcessorCacheTemplate;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@ControllerAdvice // 全局控制器增强
@RequiredArgsConstructor
public class CryptoResponseBodyAdvice implements ResponseBodyAdvice<Object> {
  private final CryptoProcessorCacheTemplate cryptoProcessorCacheTemplate;

  @Override
  public boolean supports(
      MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
    Crypto crypto = methodParameter.getMethodAnnotation(Crypto.class);
    boolean supported = ObjectUtils.isNotEmpty(crypto) && crypto.response();
    if (log.isDebugEnabled()) {
      String methodName = methodParameter.getMethod().getName();
      String className = methodParameter.getDeclaringClass().getSimpleName();
      log.debug("{} :: {} 支持加密吗? {}", className, methodName, supported);
    }
    return supported;
  }

  @Override
  public Object beforeBodyWrite(
      Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {
    // 获取加密的私钥
    String encryptedSecretKey =
        HeaderUtils.getHeader(request, DefaultConsts.HTTP_HEADER_CRYPTO_SECRET_KEY_HEX);
    if (StringUtils.isBlank(encryptedSecretKey)) {
      log.warn(
          "Failed to obtain the encryption key from the request header {}",
          DefaultConsts.HTTP_HEADER_CRYPTO_SECRET_KEY_HEX);
      return body;
    }

    CryptoProcessor cryptoProcessor =
        cryptoProcessorCacheTemplate.createIfAbsent(encryptedSecretKey);
    String bodyString = Jackson2Utils.instance.toJson(body);
    String result = cryptoProcessor.encrypt(bodyString);
    if (StringUtils.isNotBlank(result)) {
      if (log.isDebugEnabled()) {
        String methodName = returnType.getMethod().getName();
        String className = returnType.getDeclaringClass().getSimpleName();
        log.debug("{} :: {} 响应体加密完成", className, methodName);
      }
      return result;
    } else {
      return body;
    }
  }
}
