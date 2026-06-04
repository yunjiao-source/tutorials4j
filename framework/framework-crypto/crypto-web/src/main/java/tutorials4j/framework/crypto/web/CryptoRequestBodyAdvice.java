package tutorials4j.framework.crypto.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CryptoRequestBodyAdvice implements RequestBodyAdvice {
  private final CryptoRequestCacheTemplate cryptoRequestCacheTemplate;

  @Override
  public boolean supports(
      MethodParameter methodParameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    boolean supported = CryptoUtils.supported(methodParameter);
    if (log.isDebugEnabled()) {
      String methodName = methodParameter.getMethod().getName();
      String className = methodParameter.getDeclaringClass().getName();
      log.debug("[CRYPTO-WEB] 类 {} 的方法 {} 支持解密？ {}", className, methodName, supported);
    }
    return supported;
  }

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
          "[CRYPTO-WEB] 在请求头中未获取到加密密钥: headerName={}",
          DefaultConsts.HTTP_HEADER_CRYPTO_SECRET_KEY_HEX);
      return inputMessage;
    }
    CryptoProcessor cryptoProcessor = cryptoRequestCacheTemplate.createIfAbsent(encryptedSecretKey);
    // 移除可能的首尾引号（前端传参可能带引号）
    encryptedBody = encryptedBody.replaceAll("^\"|\"$", "");
    // 解密请求体
    String decryptedBody = cryptoProcessor.decrypt(encryptedBody);
    if (log.isDebugEnabled()) {
      String methodName = parameter.getMethod().getName();
      String className = parameter.getDeclaringClass().getName();
      log.debug("[CRYPTO-WEB] 类的方法执行解密完成: className={}, methodName={}", className, methodName);
    }

    // 返回解密后的请求体
    return new DecryptHttpInputMessage(
        inputMessage, decryptedBody.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public Object afterBodyRead(
      Object body,
      HttpInputMessage inputMessage,
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }

  @Override
  public Object handleEmptyBody(
      Object body,
      HttpInputMessage inputMessage,
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }

  public static class DecryptHttpInputMessage implements HttpInputMessage {

    private final HttpInputMessage httpInputMessage;
    private final byte[] data;

    public DecryptHttpInputMessage(HttpInputMessage httpInputMessage, byte[] data) {
      this.httpInputMessage = httpInputMessage;
      this.data = data;
    }

    @Override
    public InputStream getBody() throws IOException {
      return new ByteArrayInputStream(this.data);
    }

    @Override
    public HttpHeaders getHeaders() {
      return this.httpInputMessage.getHeaders();
    }
  }
}
