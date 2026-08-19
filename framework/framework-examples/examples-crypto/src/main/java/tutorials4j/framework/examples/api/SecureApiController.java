package tutorials4j.framework.examples.api;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.crypto.core.annotation.Crypto;

/**
 * 加密传输示例控制器。
 *
 * <p>演示使用 {@link Crypto} 注解对请求体进行解密、对响应体进行加密的完整链路， 前端以加密方式提交 {@link User} 数据，后端解密后原样返回。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/secure")
public class SecureApiController {
  /**
   * 接收加密提交的用户数据并返回处理结果。
   *
   * @param user 解密后的用户数据
   * @return 包含处理状态、消息及原数据的响应
   */
  @PostMapping("/submit")
  @Crypto
  public ResponseEntity<Map<String, Object>> handleEncrypted(@RequestBody User user) {
    // 业务处理...
    Map<String, Object> result = new HashMap<>();
    result.put("code", 200);
    result.put("message", "解密成功，数据已接收");
    result.put("receivedData", user);
    result.put("serverTime", LocalDateTime.now().toString());
    return ResponseEntity.ok(result);
  }

  /**
   * 用户信息记录。
   *
   * @param user 用户名
   * @param email 邮箱
   * @param message 留言内容
   * @param timestamp 提交时间
   */
  public record User(String user, String email, String message, Date timestamp) {}
}
