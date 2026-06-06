package tutorials4j.framework.feature.captcha.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.captcha.CaptchaCategory;
import tutorials4j.framework.captcha.CaptchaService;
import tutorials4j.framework.captcha.CaptchaServiceFactory;

/**
 * 验证码接口
 *
 * @author Yun Jiao
 */
@Tag(name = "验证码接口", description = "统一验证码生成与校验入口")
@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
public class CaptchaEndpoint {

  private final CaptchaServiceFactory factory;

  @Operation(summary = "生成验证码", description = "根据验证码类型生成对应的验证码数据")
  @Parameter(
      name = "category",
      description = "验证码类型",
      required = true,
      in = ParameterIn.QUERY,
      schema = @Schema(implementation = CaptchaCategory.class))
  @ApiResponse(
      responseCode = "200",
      description = "验证码生成成功",
      content =
          @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
  @GetMapping("create")
  public Map<String, Object> create(@RequestParam("category") CaptchaCategory category) {
    CaptchaService service = factory.findService(category);
    return service.draw();
  }

  @Operation(summary = "校验验证码", description = "校验用户输入的验证码是否正确")
  @ApiResponse(responseCode = "200", description = "校验结果（true/false）")
  @PostMapping("check")
  public ResponseEntity<Boolean> check(@RequestBody CaptchaRequest validate) {
    CaptchaService service = factory.findService(validate.getCategory());
    boolean isOk = service.verify(validate.getKey(), validate.getCode());
    if (isOk) {
      return ResponseEntity.ok().body(true);
    } else {
      return ResponseEntity.badRequest().body(false);
    }
  }
}
