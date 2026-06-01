package tutorials4j.framework.examples.googleauth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.spring.util.QrCodeUtils;
import tutorials4j.framework.web.security.google.TotpAuthService;

/**
 * 鉴权接口
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final TotpAuthService totpAuthService;

  /**
   * 登录并进行两步验证
   *
   * @param username 用户名
   * @param code 2FA 验证码
   * @return 登录结果
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(
      @RequestParam("username") String username, @RequestParam("code") int code) {
    // 验证 2FA 代码
    if (totpAuthService.verifyByUserName(username, code)) {
      return ResponseEntity.ok("登录成功");
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("2FA 验证失败");
    }
  }

  /**
   * 生成用户的二维码图片
   *
   * @param username 用户名
   * @return 二维码图片字节数组
   */
  @GetMapping("/generate-qr")
  public ResponseEntity<byte[]> generateQRCode(@RequestParam("username") String username) {
    String secretKey = totpAuthService.generateSecretKey(username);
    String barcodeURL = totpAuthService.getQRBarcodeURL(username, secretKey);
    byte[] qrCode = QrCodeUtils.defaultGeneratePng(barcodeURL);
    return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCode);
  }

  @GetMapping("/test-filter")
  public ResponseEntity<String> testFilter() {
    return ResponseEntity.ok().body("成功访问");
  }
}
