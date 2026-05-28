package tutorials4j.springboot3.web.apicrypto.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

/**
 * 请求体解密Advice 拦截POST/PUT等带请求体的请求，对标记@Crypto的接口解密请求体
 *
 * @author Yun Jiao
 */
@ControllerAdvice // 全局控制器增强
@RequiredArgsConstructor
public class CryptoRequestBodyAdvice implements RequestBodyAdvice {

  // 加解密处理器
  private final CryptoProcessor cryptoProcessor;
  // 加解密配置
  private final CryptoProperties properties;
  // JSON解析器
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * 判断是否需要处理当前请求
   *
   * @param parameter 方法参数
   * @param targetType 目标类型
   * @param converterType 消息转换器类型
   * @return true=需要处理，false=跳过
   */
  @Override
  public boolean supports(
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    // 条件：配置开启 + 方法有@Crypto注解 + 注解开启request解密
    return properties.isEnabled()
        && parameter.hasMethodAnnotation(Crypto.class)
        && Objects.requireNonNull(parameter.getMethodAnnotation(Crypto.class)).request();
  }

  /**
   * 读取请求体前解密处理
   *
   * @param inputMessage 原始请求消息
   * @param parameter 方法参数
   * @param targetType 目标类型
   * @param converterType 消息转换器类型
   * @return 解密后的请求消息
   * @throws IOException IO异常
   */
  @Override
  public HttpInputMessage beforeBodyRead(
      HttpInputMessage inputMessage,
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType)
      throws IOException {

    if (properties.isEnabled()) {
      // 读取原始加密请求体
      String encrypted = StreamUtils.copyToString(inputMessage.getBody(), StandardCharsets.UTF_8);
      // 移除可能的首尾引号（前端传参可能带引号）
      encrypted = encrypted.replaceAll("^\"|\"$", "");
      // 解密请求体
      String decrypted = cryptoProcessor.decrypt(encrypted);
      // 验证解密后的数据格式是否合法（JSON）
      validateDecryptedData(decrypted, targetType);
      // 返回解密后的请求体
      return new ByteArrayHttpInputMessage(
          decrypted.getBytes(StandardCharsets.UTF_8), inputMessage.getHeaders());
    }
    return inputMessage;
  }

  /**
   * 验证解密后的数据是否符合目标类型的JSON格式
   *
   * @param decrypted 解密后的字符串
   * @param targetType 目标类型
   * @throws CryptoException 数据格式异常
   */
  private void validateDecryptedData(String decrypted, Type targetType) {
    try {
      // 尝试解析JSON，验证格式合法性
      new ObjectMapper().readValue(decrypted, constructJavaType(targetType));
    } catch (IOException e) {
      throw new CryptoException("解密数据格式无效: " + e.getMessage(), e);
    }
  }

  /**
   * 构建JavaType对象（适配泛型）
   *
   * @param targetType 目标类型
   * @return JavaType
   */
  private JavaType constructJavaType(Type targetType) {
    return TypeFactory.defaultInstance().constructType(targetType);
  }

  /** 自定义HttpInputMessage实现类 用于封装解密后的请求体字节数组 */
  public static class ByteArrayHttpInputMessage implements HttpInputMessage {
    // 解密后的请求体字节数组
    private final byte[] body;
    // 请求头
    private final HttpHeaders headers;

    public ByteArrayHttpInputMessage(byte[] body, HttpHeaders headers) {
      this.body = body;
      this.headers = new HttpHeaders();
      this.headers.putAll(headers);
      // 更新Content-Length为解密后的长度
      this.headers.setContentLength(body.length);
    }

    @Override
    public InputStream getBody() throws IOException {
      // 返回字节数组输入流
      return new ByteArrayInputStream(body);
    }

    @Override
    public HttpHeaders getHeaders() {
      return headers;
    }
  }

  /** 读取请求体后处理（此处无需额外处理） */
  @Override
  public Object afterBodyRead(
      Object body,
      HttpInputMessage inputMessage,
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }

  /** 空请求体处理（此处无需额外处理） */
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
   * 解析解密后的数据（备用方法）
   *
   * @param decrypted 解密后的字符串
   * @param targetType 目标类型
   * @return 解析后的对象
   * @throws JsonProcessingException JSON解析异常
   */
  private Object parseDecryptedData(String decrypted, Type targetType)
      throws JsonProcessingException {
    return objectMapper.readValue(decrypted, objectMapper.constructType(targetType));
  }
}
