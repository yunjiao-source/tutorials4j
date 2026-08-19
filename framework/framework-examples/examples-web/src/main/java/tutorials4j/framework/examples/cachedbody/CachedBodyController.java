package tutorials4j.framework.examples.cachedbody;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.web.rest.cachedbody.CachedBodyHttpServletRequest;

/**
 * 请求体缓存示例控制器。
 *
 * <p>提供缓存与未缓存两组接口，用于演示 {@link CachedBodyHttpServletRequest} 包装后请求体可被多次读取，而未包装时第二次读取会失败的特性。
 *
 * @author yangyunjiao
 */
@Slf4j
@RestController
@RequestMapping("cached-body")
public class CachedBodyController {
  /**
   * 演示请求体被缓存后可以重复读取。
   *
   * @param request 当前 HTTP 请求
   * @return 包含包装状态与两次读取结果的 Map
   * @throws IOException 读取请求体失败时抛出
   */
  @PostMapping("cached")
  public Map<String, Object> cached(HttpServletRequest request) throws IOException {
    Map<String, Object> result = new HashMap<>();

    // 检查是否被 CachedBodyFilter 包装
    boolean isCached = request instanceof CachedBodyHttpServletRequest;
    result.put("isRequestWrapped", isCached);

    // 第一次读取请求体
    String firstRead = readRequestBody(request);
    result.put("firstRead", firstRead);

    // 第二次读取请求体
    String secondRead = readRequestBody(request);
    result.put("secondRead", secondRead);

    // 验证两次读取是否一致
    result.put("equals", firstRead.equals(secondRead));

    log.info(
        "测试结果：包装状态={}, 第一次读取={}, 第二次读取={}, 相等={}",
        isCached,
        firstRead,
        secondRead,
        firstRead.equals(secondRead));

    return result;
  }

  /**
   * 演示请求体未被缓存时第二次读取会失败（抛 {@code java.io.IOException: Stream closed}）。
   *
   * @param request 当前 HTTP 请求
   * @return 包含包装状态与首次读取结果的 Map
   * @throws IOException 读取请求体失败时抛出
   */
  @PostMapping("non-cached")
  public Map<String, Object> nonCached(HttpServletRequest request) throws IOException {
    Map<String, Object> result = new HashMap<>();

    // 检查是否被 CachedBodyFilter 包装
    boolean isCached = request instanceof CachedBodyHttpServletRequest;
    result.put("isRequestWrapped", isCached);

    String firstRead = readRequestBody(request);
    String secondRead = readRequestBody(request);
    return result;
  }

  /**
   * 从 HttpServletRequest 中读取请求体字符串。
   *
   * <p>如果请求已被 CachedBodyFilter 包装，可以重复读取；否则只能读取一次（第二次会得到空字符串或抛异常）。
   *
   * @param request HTTP 请求
   * @return 请求体字符串
   * @throws IOException 读取请求体失败时抛出
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
