package tutorials4j.framework.examples.apicrypto;

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
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/secure")
public class SecureApiController {
  @PostMapping("/submit")
  @Crypto(request = true) // 关键注解：自动解密请求体
  public ResponseEntity<Map<String, Object>> handleEncrypted(@RequestBody User user) {
    // 业务处理...
    Map<String, Object> result = new HashMap<>();
    result.put("code", 200);
    result.put("message", "解密成功，数据已接收");
    result.put("receivedData", user);
    result.put("serverTime", LocalDateTime.now().toString());
    return ResponseEntity.ok(result);
  }

  public record User(String user, String email, String message, Date timestamp) {}
}
