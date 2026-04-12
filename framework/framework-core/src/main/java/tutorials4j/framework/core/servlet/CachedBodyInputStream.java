package tutorials4j.framework.core.servlet;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 将缓存的字节数组包装为 ServletInputStream，支持多次读取。
 *
 * @author Yun Jiao
 */
public class CachedBodyInputStream extends ServletInputStream {

    private final InputStream cachedBodyInputStream;

    public CachedBodyInputStream(byte[] cachedBody) {
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
