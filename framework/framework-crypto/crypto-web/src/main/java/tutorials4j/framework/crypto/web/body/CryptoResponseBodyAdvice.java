package tutorials4j.framework.crypto.web.body;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import tutorials4j.framework.crypto.core.cache.CryptoProcessorCacheTemplate;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;
import tutorials4j.framework.crypto.core.util.CryptoUtils;

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
      MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    boolean supported = CryptoUtils.supported(returnType);
    if (log.isDebugEnabled()) {
      String methodName = returnType.getMethod().getName();
      String className = returnType.getDeclaringClass().getName();
      log.debug("[CRYPTO-WEB] 类 {} 的方法 {} 支持解密？ {}", className, methodName, supported);
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
          "[CRYPTO-WEB] 在请求头中未获取到加密密钥: headerName={}",
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
        String className = returnType.getDeclaringClass().getName();
        log.debug("[CRYPTO-WEB] 类的方法执行加密完成: className={}, methodName={}", className, methodName);
      }
      return result;
    } else {
      return body;
    }
  }
}
