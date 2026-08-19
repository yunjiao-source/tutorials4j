package tutorials4j.framework.web.rest.cachedbody;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 支持请求体多次读取的 {@link HttpServletRequest} 包装类。
 *
 * <p>此类将原始请求体的内容缓存在内存中，使得后续可以通过 {@link #getInputStream()} 和 {@link #getReader()}
 * 方法重复读取请求体内容，适用于需要多次访问请求体数据的场景（如参数校验、日志记录、签名验证等）。
 *
 * <p>缓存操作在构造时完成，使用原始请求的字符编码（未指定时默认 UTF-8）将内容转换为字节数组。
 *
 * @author Yun Jiao
 * @see HttpServletRequestWrapper
 * @see CachedServletInputStream
 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
  /** 缓存的请求体字节数组。 */
  private final byte[] cachedBody;

  /**
   * 构造一个包装器，读取并缓存原始请求的请求体。
   *
   * <p>构造完成后原始请求体已被读取完毕，后续应通过本包装类读取缓存内容。
   *
   * @param request 原始的 HttpServletRequest 对象
   * @throws IOException 如果读取原始请求体时发生 I/O 错误
   */
  public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
    super(request);
    // 读取原始请求体并缓存
    String charset = request.getCharacterEncoding();
    if (charset == null) charset = StandardCharsets.UTF_8.name();
    try (BufferedReader reader = request.getReader()) {
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
      cachedBody = sb.toString().getBytes(charset);
    }
  }

  /**
   * 返回一个可以从缓存的请求体数据中读取的 {@link ServletInputStream}。
   *
   * <p>每次调用此方法都会返回一个新的流实例，起始位置为缓存数据的开头。
   *
   * @return 基于缓存数据的输入流
   */
  @Override
  public ServletInputStream getInputStream() {
    return new CachedServletInputStream(cachedBody);
  }

  /**
   * 返回一个可以从缓存的请求体数据中读取的 {@link BufferedReader}。
   *
   * <p>默认使用平台默认字符集解码字节数据。如果需要指定字符集，请直接使用 {@link #getInputStream()} 并自行包装。
   *
   * @return 基于缓存数据的字符缓冲读取器
   */
  @Override
  public BufferedReader getReader() {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
    return new BufferedReader(new InputStreamReader(byteArrayInputStream));
  }
}
