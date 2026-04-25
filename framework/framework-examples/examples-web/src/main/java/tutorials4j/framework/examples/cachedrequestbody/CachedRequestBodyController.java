package tutorials4j.framework.examples.cachedrequestbody;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.web.http.CachedHttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求体缓存，支持多次读取
 *
 * @author yangyunjiao
 */
@Slf4j
@RestController
@RequestMapping("cached-request-body")
public class CachedRequestBodyController {
    @PostMapping("cached")
    public Map<String, Object> cached(HttpServletRequest request) throws IOException {
        Map<String, Object> result = new HashMap<>();

        // 检查是否被 CachedBodyFilter 包装
        boolean isCached = request instanceof CachedHttpServletRequestWrapper;
        result.put("isRequestWrapped", isCached);

        // 第一次读取请求体
        String firstRead = readRequestBody(request);
        result.put("firstRead", firstRead);

        // 第二次读取请求体
        String secondRead = readRequestBody(request);
        result.put("secondRead", secondRead);

        // 验证两次读取是否一致
        result.put("equals", firstRead.equals(secondRead));

        log.info("测试结果：包装状态={}, 第一次读取={}, 第二次读取={}, 相等={}",
                isCached, firstRead, secondRead, firstRead.equals(secondRead));

        return result;
    }

    /**
     * 异常： java.io.IOException: Stream closed
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("non-cached")
    public Map<String, Object> nonCached(HttpServletRequest request) throws IOException {
        Map<String, Object> result = new HashMap<>();

        // 检查是否被 CachedBodyFilter 包装
        boolean isCached = request instanceof CachedHttpServletRequestWrapper;
        result.put("isRequestWrapped", isCached);

        String firstRead = readRequestBody(request);
        String secondRead = readRequestBody(request);
        return result;
    }

    /**
     * 从 HttpServletRequest 中读取请求体字符串
     * 如果请求已被 CachedBodyFilter 包装，可以重复读取；
     * 否则只能读取一次（第二次会得到空字符串或抛异常）。
     */
    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
