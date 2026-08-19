package tutorials4j.framework.captcha.tianai.support;

import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ParamKeyEnum;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.captcha.tianai.properties.TianaiOptions;

/**
 * 天意验证码生成参数构建器。
 *
 * <p>将配置选项转换为 {@link GenerateParam} 对象。
 *
 * @author Yun Jiao
 */
@Data
@Builder
public class CaptchaGenerateParamBuilder {

  /** 背景图片格式名称 */
  private String backgroundFormatName;

  /** 模板图片格式名称 */
  private String templateFormatName;

  /** 是否混淆干扰元素 */
  private Boolean obfuscate;

  /** 验证码类型 */
  private String type;

  /** 背景图片标签 */
  private String backgroundImageTag;

  /** 模板图片标签 */
  private String templateImageTag;

  /** 字体标签 */
  private String fontTag;

  /** 容错率 */
  private Float tolerant;

  /** 点击验证码干扰元素数量 */
  private Integer clickInterferenceCount;

  /** 点击验证码需点击次数 */
  private Integer clickCheckClickCount;

  /**
   * 根据配置选项和验证码类型创建构建器实例。
   *
   * @param options 配置选项
   * @param type 验证码类型
   * @return 构建器实例
   */
  public static CaptchaGenerateParamBuilder of(TianaiOptions options, CaptchaType type) {
    return CaptchaGenerateParamBuilder.builder()
        .backgroundFormatName(options.getBackgroundFormatName())
        .templateFormatName(options.getTemplateFormatName())
        .obfuscate(options.getObfuscate())
        .type(type.name())
        .backgroundImageTag(options.getBackgroundImageTag())
        .templateImageTag(options.getTemplateImageTag())
        .fontTag(options.getFontTag())
        .tolerant(options.getTolerant())
        .clickCheckClickCount(options.getClickCheckClickCount())
        .clickInterferenceCount(options.getClickInterferenceCount())
        .build();
  }

  /**
   * 复制当前构建器。
   *
   * @return 新的构建器副本
   */
  public CaptchaGenerateParamBuilder copy() {
    return CaptchaGenerateParamBuilder.builder()
        .backgroundFormatName(this.backgroundFormatName)
        .templateFormatName(this.templateFormatName)
        .obfuscate(this.obfuscate)
        .type(this.type)
        .backgroundImageTag(this.backgroundImageTag)
        .templateImageTag(this.templateImageTag)
        .fontTag(this.fontTag)
        .tolerant(this.tolerant)
        .clickCheckClickCount(this.clickCheckClickCount)
        .clickInterferenceCount(this.clickInterferenceCount)
        .build();
  }

  /**
   * 创建 GenerateParam 对象供验证码生成使用。
   *
   * @return 生成参数
   */
  public GenerateParam createGenerateParam() {
    GenerateParam generateParam = new GenerateParam();
    generateParam.setBackgroundFormatName(backgroundFormatName);
    generateParam.setTemplateFormatName(templateFormatName);
    generateParam.setObfuscate(obfuscate);
    generateParam.setType(type);
    generateParam.setBackgroundImageTag(backgroundImageTag);
    generateParam.setTemplateImageTag(templateImageTag);
    if (StringUtils.isNotBlank(fontTag)) {
      generateParam.addParam(ParamKeyEnum.FONT_TAG, fontTag);
    }
    if (tolerant != null) {
      generateParam.addParam(ParamKeyEnum.TOLERANT, tolerant);
    }
    if (clickInterferenceCount != null) {
      generateParam.addParam(ParamKeyEnum.CLICK_INTERFERENCE_COUNT, clickInterferenceCount);
    }
    if (clickCheckClickCount != null) {
      generateParam.addParam(ParamKeyEnum.CLICK_CHECK_CLICK_COUNT, clickCheckClickCount);
    }
    return generateParam;
  }
}
