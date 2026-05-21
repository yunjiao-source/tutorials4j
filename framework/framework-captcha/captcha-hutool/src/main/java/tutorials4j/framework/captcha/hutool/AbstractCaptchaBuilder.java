package tutorials4j.framework.captcha.hutool;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import java.awt.Font;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tutorials4j.framework.common.core.bean.ColorTypeEnum;

/**
 * 抽象验证码构建器，用于配置和创建Hutool验证码实例。
 *
 * <p>定义了验证码的通用属性（宽度、高度、干扰元素数量、背景色、文字透明度、模糊度、字体、码生成器、是否忽略大小写等）。 子类需实现{@link
 * #createCaptcha()}以创建具体类型的验证码。
 *
 * @param <C> 具体的Hutool验证码类型，如{@link cn.hutool.captcha.LineCaptcha}
 * @author Yun Jiao
 */
@Getter
@Setter
@Accessors(fluent = true, chain = true)
public abstract class AbstractCaptchaBuilder<C extends AbstractCaptcha> {
  /** 图片的宽度 */
  private Integer width;

  /** 图片的高度 */
  private Integer height;

  /** 验证码干扰元素个数（干扰线宽度） */
  private Integer interfereCount;

  /** 背景色 */
  private ColorTypeEnum backgroundColor;

  /** 文字透明度，取值0~1，1表示不透明 */
  private Float textAlpha;

  /** 模糊度（0 - 30） */
  private Integer fuzziness;

  /** 字体 */
  private Font font;

  /** 码生成器 */
  private CodeGenerator generator;

  /** 校验时是否忽略大小写 */
  private Boolean validIgnoreCase;

  /**
   * 创建验证码工具，子类实现
   *
   * @return 实例
   */
  protected abstract C createCaptcha();

  /**
   * 填充验证码工具的通用属性（字体、背景等）。
   *
   * @param captcha 验证码实例，不能为null
   */
  protected void fill(AbstractCaptcha captcha) {
    captcha.setFont(font);
    captcha.setBackground(backgroundColor.getMapping());

    if (textAlpha != null) {
      captcha.setTextAlpha(textAlpha);
    }
  }

  /**
   * 构建验证码实例：先调用{@link #createCaptcha()}创建，再调用{@link #fill(AbstractCaptcha)}填充属性。
   *
   * @return 配置完成的验证码实例
   */
  public C build() {
    C captcha = createCaptcha();
    fill(captcha);
    return captcha;
  }
}
