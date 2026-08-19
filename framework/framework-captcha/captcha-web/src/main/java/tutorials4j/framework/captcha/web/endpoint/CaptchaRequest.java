package tutorials4j.framework.captcha.web.endpoint;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tutorials4j.framework.captcha.support.CaptchaCategory;

/**
 * 验证码校验请求对象。
 *
 * <p>封装验证码唯一标识、用户输入的验证码值以及验证码类型，用于统一验证码校验接口的入参。
 *
 * @author Yun Jiao
 */
@Data
@Schema(description = "验证码校验请求对象")
public class CaptchaRequest {

  /** 验证码唯一标识（通常为 UUID），由生成接口返回。 */
  @Schema(
      description = "验证码唯一标识（通常为 UUID）",
      example = "c2b3e8f0-9c3d-11ee-b9d1-0242ac120002",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String key;

  /** 用户输入的验证码值。 */
  @Schema(description = "用户输入的验证码值", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
  private String code;

  /** 验证码类型。 */
  @Schema(description = "验证码类型", requiredMode = Schema.RequiredMode.REQUIRED)
  private CaptchaCategory category;
}
