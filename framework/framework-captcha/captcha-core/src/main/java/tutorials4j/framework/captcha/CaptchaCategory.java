package tutorials4j.framework.captcha;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

/**
 * 验证码类别
 *
 * @author Yun Jiao
 */
@Getter
@ToString
@Schema(description = "验证码类型")
public enum CaptchaCategory {
  @Schema(description = "滑动还原验证码（PNG）")
  TIANAI_CONCAT("png", "滑动还原验证码"),

  @Schema(description = "旋转验证码（PNG）")
  TIANAI_ROTATE("png", "旋转验证码"),

  @Schema(description = "点选验证码（PNG）")
  TIANAI_WORD_IMAGE_CLICK("png", "点选验证码"),

  @Schema(description = "滑块验证码（PNG）")
  TIANAI_SLIDER("png", "滑块验证码"),

  @Schema(description = "线段干扰验证码（PNG）")
  HUTOOL_LINE("png", "线段干扰验证码"),

  @Schema(description = "圆圈干扰验证码（PNG）")
  HUTOOL_CIRCLE("png", "圆圈干扰验证码"),

  @Schema(description = "扭曲干扰验证码（PNG）")
  HUTOOL_SHEAR("png", "扭曲干扰验证码"),

  @Schema(description = "GIF验证码（GIF）")
  HUTOOL_GIF("gif", "GIF验证码");

  private final String ext;
  private final String description;

  CaptchaCategory(String ext, String description) {
    this.ext = ext;
    this.description = description;
  }
}
