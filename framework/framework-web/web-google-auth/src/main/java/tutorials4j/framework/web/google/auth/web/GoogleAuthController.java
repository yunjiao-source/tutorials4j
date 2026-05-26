package tutorials4j.framework.web.google.auth.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.spring.util.QrCodeUtils;
import tutorials4j.framework.web.google.auth.GoogleAuthService;

/**
 * Google Authenticator 管理接口，提供 TOTP 校验、二维码生成等 REST API。
 *
 * <p>所有接口路径以 {@code /t4j/google-auth} 为前缀。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("t4j/google-auth")
@RequiredArgsConstructor
@Tag(name = "Google Authenticator", description = "TOTP 双因素认证接口")
public class GoogleAuthController {

  private final GoogleAuthService googleAuthService;

  /**
   * 校验用户提供的 TOTP 验证码。
   *
   * @param username 用户名
   * @param code TOTP 验证码（6位数字）
   * @return 包含校验结果文本的响应
   */
  @PostMapping("/check")
  @Operation(summary = "校验 TOTP 验证码", description = "根据用户名和动态验证码进行身份校验")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "校验成功或失败（返回文本信息）",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "参数不完整或格式错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
      })
  public ResponseEntity<?> check(
      @Parameter(description = "用户名", required = true, example = "admin") @RequestParam("username")
          String username,
      @Parameter(description = "TOTP 动态验证码（6位数字）", required = true, example = "123456")
          @RequestParam("code")
          int code) {
    boolean sucess = googleAuthService.verifyByUserName(username, code);
    if (sucess) {
      return ResponseEntity.ok("Google Auth 校验成功");
    } else {
      return ResponseEntity.ok("Google Auth 校验失败");
    }
  }

  /**
   * 为指定用户生成 TOTP 二维码（PNG 格式），用于客户端扫描绑定。
   *
   * <p>调用后会为该用户生成新的秘钥，并返回二维码图片。
   *
   * @param username 用户名
   * @return PNG 格式的二维码图片字节数组
   */
  @GetMapping("/generate/qr")
  @Operation(summary = "生成 TOTP 绑定二维码", description = "为指定用户生成新的秘钥并返回二维码图片（PNG格式）")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "成功返回二维码图片",
            content =
                @Content(
                    mediaType = "image/png",
                    schema = @Schema(type = "string", format = "binary"))),
        @ApiResponse(responseCode = "400", description = "用户名无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
      })
  public ResponseEntity<byte[]> generateQRCode(
      @Parameter(description = "用户名", required = true, example = "admin") @RequestParam("username")
          String username) {
    String secretKey = googleAuthService.generateSecretKey(username);
    String barcodeURL = googleAuthService.getQRBarcodeURL(username, secretKey);
    byte[] qrCode = QrCodeUtils.defaultGeneratePng(barcodeURL);
    return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCode);
  }
}
