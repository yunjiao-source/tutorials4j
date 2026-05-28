package tutorials4j.springboot3.web.apicrypto.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tutorials4j.springboot3.web.Result;

/**
 * 响应体加密Advice 拦截控制器响应，对标记@Crypto的接口加密响应体
 *
 * @author Yun Jiao
 */
@ControllerAdvice // 全局控制器增强
@RequiredArgsConstructor
public class CryptoResponseBodyAdvice implements ResponseBodyAdvice<Object> {

  // 加解密处理器
  private final CryptoProcessor cryptoProcessor;
  // 加解密配置
  private final CryptoProperties properties;
  // JSON解析器
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * 判断是否需要处理当前响应
   *
   * @param returnType 返回值类型
   * @param converterType 消息转换器类型
   * @return true=需要处理，false=跳过
   */
  @Override
  public boolean supports(
      MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    // 条件：配置开启 + (方法有@Crypto注解且开启response加密 或 异常响应)
    return (properties.isEnabled()
            && ((returnType.hasMethodAnnotation(Crypto.class)
                && Objects.requireNonNull(returnType.getMethodAnnotation(Crypto.class)).response()))
        || isExceptionResponse(returnType));
  }

  /**
   * 判断是否为异常响应
   *
   * @param returnType 返回值类型
   * @return true=异常响应，false=正常响应
   */
  private boolean isExceptionResponse(MethodParameter returnType) {
    // 异常处理类通常标记@ResponseBody
    return Objects.requireNonNull(returnType.getMethod())
        .getDeclaringClass()
        .isAnnotationPresent(ResponseBody.class);
  }

  /**
   * 返回响应体前加密处理
   *
   * @param body 原始响应体
   * @param returnType 返回值类型
   * @param selectedContentType 响应媒体类型
   * @param selectedConverterType 消息转换器类型
   * @param request 服务端请求
   * @param response 服务端响应
   * @return 加密后的响应体
   */
  @Override
  public Object beforeBodyWrite(
      Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {

    try {
      // 仅对自定义Result类型的响应加密（适配业务统一返回格式）
      if (body instanceof Result result && properties.isEnabled()) {
        // 添加响应头，标记响应已加密
        response.getHeaders().add("x-encrypt-response", "AES");
        response.getHeaders().add("x-encrypt-error", "AES");
        // 将响应体转为JSON字符串
        String rawData = objectMapper.writeValueAsString(result);
        // 加密并返回密文
        return cryptoProcessor.encrypt(rawData);
      }
    } catch (Exception e) {
      throw new CryptoException("异常响应加密失败", e);
    }
    // 非Result类型直接返回原始数据
    return body;
  }
}
