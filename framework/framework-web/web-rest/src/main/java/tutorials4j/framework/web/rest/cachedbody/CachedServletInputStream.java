package tutorials4j.framework.web.rest.cachedbody;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 基于字节数组的 {@link ServletInputStream} 实现，支持多次读取。
 *
 * <p>该类内部使用 {@link ByteArrayInputStream} 来存储缓存的请求体数据，因此可以从头开始反复读取。 所有读取操作都委托给内部的字节数组流。
 *
 * <p>注意：当前实现不支持异步非阻塞 I/O，调用 {@link #setReadListener(ReadListener)} 会抛出 {@link
 * UnsupportedOperationException}。
 *
 * @author Yun Jiao
 * @see ServletInputStream
 * @see ByteArrayInputStream
 */
public class CachedServletInputStream extends ServletInputStream {

  /** 底层的缓存字节数组输入流。 */
  private final InputStream cachedBodyInputStream;

  /**
   * 使用给定的字节数组创建输入流。
   *
   * @param cachedBody 缓存的请求体字节数组
   */
  public CachedServletInputStream(byte[] cachedBody) {
    // 使用 ByteArrayInputStream 作为底层实现
    this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
  }

  /** 判断流是否已读取完毕，当底层可用字节数为 0 时返回 {@code true}。 */
  @Override
  public boolean isFinished() {
    try {
      // 如果可用字节数为0，说明流已读完
      return cachedBodyInputStream.available() == 0;
    } catch (IOException e) {
      return true;
    }
  }

  /** 数据全部缓存在内存中，因此流始终处于就绪状态。 */
  @Override
  public boolean isReady() {
    // 因为所有数据都在内存中，所以总是就绪
    return true;
  }

  /** 当前实现不支持异步非阻塞读取，调用时抛出 {@link UnsupportedOperationException}。 */
  @Override
  public void setReadListener(ReadListener readListener) {
    // 本例不支持异步非阻塞读取，若需要可自行扩展
    throw new UnsupportedOperationException("不支持异步非阻塞读取");
  }

  /** 从底层缓存流中读取下一个字节。 */
  @Override
  public int read() throws IOException {
    return cachedBodyInputStream.read();
  }

  /** 从底层缓存流中读取最多 {@code len} 个字节存入给定数组。 */
  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return cachedBodyInputStream.read(b, off, len);
  }

  /** 关闭底层缓存输入流。 */
  @Override
  public void close() throws IOException {
    cachedBodyInputStream.close();
  }
}
