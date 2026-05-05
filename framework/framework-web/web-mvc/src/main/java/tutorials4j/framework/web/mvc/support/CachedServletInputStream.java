package tutorials4j.framework.web.mvc.support;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 基于字节数组的 {@link ServletInputStream} 实现，支持多次读取。
 *
 * <p>该类内部使用 {@link ByteArrayInputStream} 来存储缓存的请求体数据，因此可以从头开始反复读取。
 * 所有读取操作都委托给内部的字节数组流。
 *
 * <p>注意：当前实现不支持异步非阻塞 I/O，调用 {@link #setReadListener(ReadListener)} 会抛出
 * {@link UnsupportedOperationException}。
 *
 * @author Yun Jiao
 * @see ServletInputStream
 * @see ByteArrayInputStream
 */
public class CachedServletInputStream extends ServletInputStream {

    private final InputStream cachedBodyInputStream;

    public CachedServletInputStream(byte[] cachedBody) {
        // 使用 ByteArrayInputStream 作为底层实现
        this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
    }

    @Override
    public boolean isFinished() {
        try {
            // 如果可用字节数为0，说明流已读完
            return cachedBodyInputStream.available() == 0;
        } catch (IOException e) {
            return true;
        }
    }

    @Override
    public boolean isReady() {
        // 因为所有数据都在内存中，所以总是就绪
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        // 本例不支持异步非阻塞读取，若需要可自行扩展
        throw new UnsupportedOperationException("不支持异步非阻塞读取");
    }

    @Override
    public int read() throws IOException {
        return cachedBodyInputStream.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return cachedBodyInputStream.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
        cachedBodyInputStream.close();
    }
}
