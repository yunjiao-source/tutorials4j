package tutorials4j.framework.examples.signature;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.web.core.annotation.RequiredSignature;

/**
 * 示例接口
 *
 * @author yangyunjiao
 */
@Slf4j
@RestController
@RequestMapping("signature")
@RequiredArgsConstructor
public class SignatureController {
  private final SignatureClient signatureClient;

  @GetMapping("test")
  public String test(@RequestParam("userid") String userId, @RequestParam("amount") Long amount) {
    return signatureClient.test(userId, amount);
  }

  @RequiredSignature(timeWindowSeconds = 60)
  @PostMapping("/pay")
  public String pay(@RequestBody PayRequest request) {
    // 支付逻辑
    return "PAIED, userId=" + request.userId + " amount=" + request.amount;
  }

  public record PayRequest(String userId, Long amount) {}
  ;
}
