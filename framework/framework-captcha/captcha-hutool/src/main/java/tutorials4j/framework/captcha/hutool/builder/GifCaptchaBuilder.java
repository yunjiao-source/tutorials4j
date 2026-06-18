package tutorials4j.framework.captcha.hutool.builder;

import cn.hutool.captcha.GifCaptcha;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Gif验证码 创建器
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@Accessors(fluent = true, chain = true)
public class GifCaptchaBuilder extends AbstractCaptchaBuilder<GifCaptcha> {
  /** 量化器取样间隔, 1 ~ 20 值之间 - 默认是10ms */
  private Integer quality;

  /** 帧循环次数，默认是 0， 意味着无限循环 */
  private Integer repeat;

  /** 设置随机颜色时，最小的取色范围 */
  private Integer minColor;

  /** 设置随机颜色时，最大的取色范围 */
  private Integer maxColor;

  @Override
  protected GifCaptcha createCaptcha() {
    GifCaptcha gifCaptcha = new GifCaptcha(width(), height(), generator(), interfereCount());
    gifCaptcha.setQuality(quality);
    gifCaptcha.setRepeat(repeat);
    gifCaptcha.setMaxColor(maxColor);
    gifCaptcha.setMinColor(minColor);
    return gifCaptcha;
  }
}
