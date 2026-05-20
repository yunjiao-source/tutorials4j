package tutorials4j.framework.captcha.tianai;

import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ParamKeyEnum;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.captcha.properties.TianaiOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@Builder
public class TianAiCaptchaGenerateParamBuilder {
  private String backgroundFormatName;
  private String templateFormatName;
  private Boolean obfuscate;
  private String type;
  private String backgroundImageTag;
  private String templateImageTag;
  private String fontTag;
  private Float tolerant;
  private Integer clickInterferenceCount;
  private Integer clickCheckClickCount;

  public static TianAiCaptchaGenerateParamBuilder of(TianaiOptions options, CaptchaType type) {
    return TianAiCaptchaGenerateParamBuilder.builder()
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

  public TianAiCaptchaGenerateParamBuilder copy() {
    return TianAiCaptchaGenerateParamBuilder.builder()
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
