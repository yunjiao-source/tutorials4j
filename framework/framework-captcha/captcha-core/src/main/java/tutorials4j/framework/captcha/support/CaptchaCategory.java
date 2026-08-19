package tutorials4j.framework.captcha.support;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 验证码类别枚举。
 *
 * <p>定义系统支持的各类验证码（滑动还原、旋转、点选、滑块及 Hutool 图形验证码等）， 并关联对应的文件扩展名与中文描述。
 *
 * @author Yun Jiao
 */
@Getter
public enum CaptchaCategory {
  /** 滑动还原验证码（PNG）。 */
  @Schema(description = "滑动还原验证码（PNG）")
  TIANAI_CONCAT("png", "滑动还原验证码"),

  /** 旋转验证码（PNG）。 */
  @Schema(description = "旋转验证码（PNG）")
  TIANAI_ROTATE("png", "旋转验证码"),

  /** 点选验证码（PNG）。 */
  @Schema(description = "点选验证码（PNG）")
  TIANAI_WORD_IMAGE_CLICK("png", "点选验证码"),

  /** 滑块验证码（PNG）。 */
  @Schema(description = "滑块验证码（PNG）")
  TIANAI_SLIDER("png", "滑块验证码"),

  /** 线段干扰验证码（PNG）。 */
  @Schema(description = "线段干扰验证码（PNG）")
  HUTOOL_LINE("png", "线段干扰验证码"),

  /** 圆圈干扰验证码（PNG）。 */
  @Schema(description = "圆圈干扰验证码（PNG）")
  HUTOOL_CIRCLE("png", "圆圈干扰验证码"),

  /** 扭曲干扰验证码（PNG）。 */
  @Schema(description = "扭曲干扰验证码（PNG）")
  HUTOOL_SHEAR("png", "扭曲干扰验证码"),

  /** GIF 验证码（GIF）。 */
  @Schema(description = "GIF验证码（GIF）")
  HUTOOL_GIF("gif", "GIF验证码");

  /** 验证码文件扩展名。 */
  private final String ext;

  /** 验证码中文描述。 */
  private final String description;

  CaptchaCategory(String ext, String description) {
    this.ext = ext;
    this.description = description;
  }
}
