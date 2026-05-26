package tutorials4j.springboot3.integration.restsignature;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例接口
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/signature")
public class DemoController {

  @PostMapping("/create")
  public String create() {
    // 业务逻辑
    return "create";
  }

  @RequireSignature(timeWindow = 60, checkNonce = true)
  @PostMapping("/pay")
  public String pay(@RequestBody PayRequest request) {
    // 支付逻辑
    return "PAIED, userId=" + request.userId + " amount=" + request.amount;
  }

  public record PayRequest(String userId, Long amount) {}
  ;
}
