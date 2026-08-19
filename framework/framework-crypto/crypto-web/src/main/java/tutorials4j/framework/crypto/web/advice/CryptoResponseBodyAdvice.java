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
import tutorials4j.framework.common.spring.jackson.JacksonUtils;
import tutorials4j.framework.common.spring.util.HeaderUtils;
import tutorials4j.framework.crypto.core.annotation.Crypto;
import tutorials4j.framework.crypto.core.cache.CryptoProcessorCacheTemplate;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;

/**
 * 响应体加密增强器。
 *
 * <p>针对标注了 {@link Crypto} 且 {@code response()} 为 true 的接口，在响应体写出前 从请求头获取加密后的会话密钥，通过 {@link
 * CryptoProcessor} 将响应内容加密后返回， 从而保证敏感响应数据的传输安全。
 *
 * @author Yun Jiao
 */
@Slf4j
@ControllerAdvice // 全局控制器增强
@RequiredArgsConstructor
public class CryptoResponseBodyAdvice implements ResponseBodyAdvice<Object> {
  private final CryptoProcessorCacheTemplate cryptoProcessorCacheTemplate;

  /**
   * 判断当前响应是否需要进行加密处理。
   *
   * @param methodParameter 处理方法参数
   * @param converterType 消息转换器类型
   * @return 若方法标注了 {@link Crypto} 且开启响应加密则返回 true
   */
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

  /**
   * 在响应体写出前加密响应内容。
   *
   * <p>若请求头中缺少加密密钥，则直接返回原始响应体；若加密结果为空，同样返回原始响应体。
   *
   * @param body 原始响应体
   * @param returnType 处理方法返回类型
   * @param selectedContentType 选定的内容类型
   * @param selectedConverterType 选定的消息转换器类型
   * @param request 服务器请求
   * @param response 服务器响应
   * @return 加密后的响应字符串；无需加密时返回原始响应体
   */
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
    String bodyString = JacksonUtils.instance.toJson(body);
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
