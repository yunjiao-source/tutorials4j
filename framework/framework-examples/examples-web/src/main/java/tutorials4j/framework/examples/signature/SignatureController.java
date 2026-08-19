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
 * 签名示例接口。
 *
 * <p>提供签名测试与签名校验接口的演示。
 *
 * @author yangyunjiao
 */
@Slf4j
@RestController
@RequestMapping("signature")
@RequiredArgsConstructor
public class SignatureController {
  private final SignatureClient signatureClient;

  /**
   * 调用签名客户端测试签名接口。
   *
   * @param userId 用户 ID
   * @param amount 金额
   * @return 调用结果
   */
  @GetMapping("test")
  public String test(@RequestParam("userid") String userId, @RequestParam("amount") Long amount) {
    return signatureClient.test(userId, amount);
  }

  /**
   * 支付接口，需要携带有效的请求签名。
   *
   * @param request 支付请求参数
   * @return 支付结果
   */
  @RequiredSignature(timeWindowSeconds = 60)
  @PostMapping("/pay")
  public String pay(@RequestBody PayRequest request) {
    // 支付逻辑
    return "PAIED, userId=" + request.userId + " amount=" + request.amount;
  }

  /**
   * 支付请求参数。
   *
   * @param userId 用户 ID
   * @param amount 支付金额
   */
  public record PayRequest(String userId, Long amount) {}
  ;
}
