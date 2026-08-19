package tutorials4j.framework.captcha.web.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.captcha.support.CaptchaCategory;
import tutorials4j.framework.captcha.support.CaptchaService;
import tutorials4j.framework.captcha.support.CaptchaServiceFactory;
import tutorials4j.framework.common.core.bean.Result;

/**
 * 统一验证码接口控制器。
 *
 * <p>提供统一的验证码生成与校验 REST API，根据 {@link CaptchaCategory} 分发到对应的 {@link CaptchaService} 实现。
 *
 * @author Yun Jiao
 */
@Tag(name = "验证码接口", description = "统一验证码生成与校验入口")
@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
public class UnifiedCaptchaEndpoint {

  private final CaptchaServiceFactory factory;

  /**
   * 生成验证码。
   *
   * @param category 验证码类型
   * @return 包含验证码数据的生成结果
   */
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
  public Result<Map<String, Object>> create(@RequestParam("category") CaptchaCategory category) {
    CaptchaService service = factory.findService(category);
    return Result.success(service.draw());
  }

  /**
   * 校验用户输入的验证码是否正确。
   *
   * @param validate 验证码校验请求对象
   * @return 校验结果，true 表示通过
   */
  @Operation(summary = "校验验证码", description = "校验用户输入的验证码是否正确")
  @ApiResponse(responseCode = "200", description = "校验结果（true/false）")
  @PostMapping("check")
  public Result<Boolean> check(@RequestBody CaptchaRequest validate) {
    CaptchaService service = factory.findService(validate.getCategory());
    boolean isOk = service.verify(validate.getKey(), validate.getCode());
    return Result.success(isOk);
  }
}
