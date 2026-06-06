package tutorials4j.framework.feature.rest.captcha;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.captcha.CaptchaCategory;
import tutorials4j.framework.captcha.CaptchaService;
import tutorials4j.framework.captcha.CaptchaServiceFactory;

/**
 * 天意验证码控制器，提供生成和校验验证码的 REST API。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/captcha/tianai")
@RequiredArgsConstructor
@Tag(name = "天意验证码接口", description = "生成滑块、旋转等行为验证码，并校验用户轨迹")
public class TianaiCaptchaEndpoint {
  private final CaptchaServiceFactory factory;
  private final ImageCaptchaApplication imageCaptchaApplication;

  /**
   * 生成验证码。
   *
   * @param type 验证码类型，可选
   * @return 包含验证码图片及数据的响应
   */
  @Operation(summary = "生成验证码", description = "根据验证码类型生成对应的行为验证码，返回验证码ID及图片Base64数据")
  @Parameter(
      name = "type",
      description = "验证码类型（滑动、旋转等）",
      required = false,
      schema = @Schema(implementation = CaptchaCategory.class))
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "验证码生成成功",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)))
  })
  @PostMapping("gen")
  public ApiResponse<Map<String, Object>> gen(@RequestParam(value = "type") CaptchaCategory type) {
    CaptchaService service = factory.findService(type);
    return ApiResponse.ofSuccess(service.draw());
  }

  /**
   * 校验验证码。
   *
   * @param data 请求体，包含验证码 id 和用户轨迹数据
   * @return 校验结果响应
   */
  @Operation(summary = "校验验证码", description = "提交验证码ID及用户行为轨迹，校验是否通过")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "校验成功",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "校验失败或参数异常")
  })
  @PostMapping("check")
  public ApiResponse<?> check(@RequestBody Data data) {
    ApiResponse<?> response = imageCaptchaApplication.matching(data.getId(), data.getData());
    if (response.isSuccess()) {
      return ApiResponse.ofSuccess(Collections.singletonMap("id", data.getId()));
    }
    return response;
  }

  /** 验证码校验请求数据封装。 */
  @lombok.Data
  @Schema(description = "验证码校验请求参数")
  public static class Data {

    @Schema(
        description = "验证码ID（由生成接口返回）",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "captcha_123456")
    private String id;

    @Schema(description = "用户交互轨迹数据", requiredMode = Schema.RequiredMode.REQUIRED)
    private ImageCaptchaTrack data;
  }
}
