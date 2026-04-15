package tutorials4j.framework.web.core.servlet;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 支持多次读取的包装类
 *
 * @author Yun Jiao
 */
public class CachedHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private byte[] cachedBody;

    public CachedHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
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

    @Override
    public ServletInputStream getInputStream() {
        return new CachedServletInputStream(cachedBody);
    }

    @Override
    public BufferedReader getReader() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }
}
