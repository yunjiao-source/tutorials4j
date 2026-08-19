package tutorials4j.framework.crypto.web.advice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.spring.util.HeaderUtils;
import tutorials4j.framework.crypto.core.annotation.Crypto;
import tutorials4j.framework.crypto.core.cache.CryptoProcessorCacheTemplate;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;

/**
 * 请求体解密增强器。
 *
 * <p>针对标注了 {@link Crypto} 且 {@code request()} 为 true 的接口，在请求体反序列化前 读取加密的请求内容，从请求头获取加密后的会话密钥，并通过
 * {@link CryptoProcessor} 解密后 替换为明文请求体，供后续消息转换器正常解析。
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CryptoRequestBodyAdvice implements RequestBodyAdvice {
  private final CryptoProcessorCacheTemplate cryptoProcessorCacheTemplate;

  /**
   * 判断当前请求是否需要进行解密处理。
   *
   * @param methodParameter 处理方法参数
   * @param targetType 目标类型
   * @param converterType 消息转换器类型
   * @return 若方法标注了 {@link Crypto} 且开启请求解密则返回 true
   */
  @Override
  public boolean supports(
      MethodParameter methodParameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    Crypto crypto = methodParameter.getMethodAnnotation(Crypto.class);
    boolean supported = ObjectUtils.isNotEmpty(crypto) && crypto.request();
    if (log.isDebugEnabled()) {
      String methodName = methodParameter.getMethod().getName();
      String className = methodParameter.getDeclaringClass().getSimpleName();
      log.debug("{} :: {} 支持解密吗? {}", className, methodName, supported);
    }
    return supported;
  }

  /**
   * 在请求体读取前解密加密的请求内容。
   *
   * <p>若请求体为空或请求头中缺少加密密钥，则直接返回原始请求消息。
   *
   * @param inputMessage 原始请求消息
   * @param parameter 处理方法参数
   * @param targetType 目标类型
   * @param converterType 消息转换器类型
   * @return 解密后的请求消息；无需解密时返回原始消息
   * @throws IOException 读取或解密过程中发生 I/O 异常
   */
  @Override
  public HttpInputMessage beforeBodyRead(
      HttpInputMessage inputMessage,
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType)
      throws IOException {
    // 读取原始加密请求体
    String encryptedBody = StreamUtils.copyToString(inputMessage.getBody(), StandardCharsets.UTF_8);
    if (StringUtils.isBlank(encryptedBody)) {
      return inputMessage;
    }

    // 获取加密的私钥
    String encryptedSecretKey =
        HeaderUtils.getHeader(
            inputMessage.getHeaders(), DefaultConsts.HTTP_HEADER_CRYPTO_SECRET_KEY_HEX);
    if (StringUtils.isBlank(encryptedSecretKey)) {
      log.warn(
          "[CRYPTO-WEB] Failed to obtain the encryption key from the request header {}",
          DefaultConsts.HTTP_HEADER_CRYPTO_SECRET_KEY_HEX);
      return inputMessage;
    }
    CryptoProcessor cryptoProcessor =
        cryptoProcessorCacheTemplate.createIfAbsent(encryptedSecretKey);
    // 移除可能的首尾引号（前端传参可能带引号）
    encryptedBody = encryptedBody.replaceAll("^\"|\"$", "");
    // 解密请求体
    String decryptedBody = cryptoProcessor.decrypt(encryptedBody);
    if (log.isDebugEnabled()) {
      String methodName = parameter.getMethod().getName();
      String className = parameter.getDeclaringClass().getSimpleName();
      log.debug("{} :: {} 请求体解密完成", className, methodName);
    }

    // 返回解密后的请求体
    return new DecryptHttpInputMessage(
        inputMessage, decryptedBody.getBytes(StandardCharsets.UTF_8));
  }

  /** 请求体读取完成后直接返回原对象，不做额外处理。 */
  @Override
  public Object afterBodyRead(
      Object body,
      HttpInputMessage inputMessage,
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }

  /** 请求体为空时直接返回原对象，不做额外处理。 */
  @Override
  public Object handleEmptyBody(
      Object body,
      HttpInputMessage inputMessage,
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }

  /**
   * 携带解密后请求体的 {@link HttpInputMessage} 实现。
   *
   * <p>包装原始请求消息，以解密后的字节数据作为请求体内容，同时透传原始请求头。
   */
  public static class DecryptHttpInputMessage implements HttpInputMessage {

    private final HttpInputMessage httpInputMessage;
    private final byte[] data;

    /**
     * 构造解密后的请求消息。
     *
     * @param httpInputMessage 原始请求消息
     * @param data 解密后的请求体字节数据
     */
    public DecryptHttpInputMessage(HttpInputMessage httpInputMessage, byte[] data) {
      this.httpInputMessage = httpInputMessage;
      this.data = data;
    }

    /** 返回解密后的请求体输入流。 */
    @Override
    public InputStream getBody() throws IOException {
      return new ByteArrayInputStream(this.data);
    }

    /** 返回原始请求的头信息。 */
    @Override
    public HttpHeaders getHeaders() {
      return this.httpInputMessage.getHeaders();
    }
  }
}
