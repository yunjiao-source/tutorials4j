package tutorials4j.framework.crypto.web;

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
import tutorials4j.framework.crypto.core.annotation.Crypto;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class CryptoRequestBodyAdvice implements RequestBodyAdvice {
  private final CryptoProcessor cryptoProcessor;

  @Override
  public boolean supports(
      MethodParameter methodParameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    Crypto crypto = methodParameter.getMethodAnnotation(Crypto.class);
    boolean isSupports = ObjectUtils.isNotEmpty(crypto) && crypto.request();

    if (log.isDebugEnabled()) {
      String methodName = methodParameter.getMethod().getName();
      String className = methodParameter.getDeclaringClass().getName();
      log.debug("[CRYPTO-WEB] 类 {} 的方法 {} 支持解密？ {}", className, methodName, isSupports);
    }
    return isSupports;
  }

  @Override
  public HttpInputMessage beforeBodyRead(
      HttpInputMessage inputMessage,
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType)
      throws IOException {
    // 读取原始加密请求体
    String encrypted = StreamUtils.copyToString(inputMessage.getBody(), StandardCharsets.UTF_8);
    if (StringUtils.isBlank(encrypted)) {
      return inputMessage;
    }
    // 移除可能的首尾引号（前端传参可能带引号）
    encrypted = encrypted.replaceAll("^\"|\"$", "");
    // 解密请求体
    String decrypted = cryptoProcessor.decrypt(encrypted);
    if (log.isDebugEnabled()) {
      String methodName = parameter.getMethod().getName();
      String className = parameter.getDeclaringClass().getName();
      log.debug("[CRYPTO-WEB] 类 {} 的方法 {} 执行解密完成", className, methodName);
    }

    // 返回解密后的请求体
    return new DecryptHttpInputMessage(inputMessage, decrypted.getBytes(StandardCharsets.UTF_8));
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
