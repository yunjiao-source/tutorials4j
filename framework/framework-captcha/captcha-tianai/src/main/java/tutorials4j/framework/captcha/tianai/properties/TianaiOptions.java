package tutorials4j.framework.captcha.tianai.properties;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 天意验证码的配置选项，支持不同验证码类型的独立配置及与公共配置的合并。
 *
 * <p>各验证码类型的独立配置可通过 {@link #merge(TianaiOptions)} 与公共配置合并， 当前配置中为空的属性会回退使用公共配置的值。
 *
 * @author Yun Jiao
 */
@Data
public class TianaiOptions {

  /** 背景图片格式名称，如 "jpeg" */
  private String backgroundFormatName;

  /** 模板图片格式名称，如 "png" */
  private String templateFormatName;

  /** 是否混淆 */
  private Boolean obfuscate;

  /** 背景图片标签，用于资源定位 */
  private String backgroundImageTag;

  /** 模板图片标签 */
  private String templateImageTag;

  /** 字体标签 */
  private String fontTag;

  /** 容差阈值 */
  private Float tolerant;

  /** 点选验证码干扰项数量 */
  private Integer clickInterferenceCount;

  /** 点选验证码需点击的正确数量 */
  private Integer clickCheckClickCount;

  /** 默认构造器，创建一个所有属性均为空的配置实例。 */
  public TianaiOptions() {}

  /**
   * 便捷构造器，设置基本选项。
   *
   * @param backgroundFormatName 背景图片格式
   * @param templateFormatName 模板图片格式
   * @param obfuscate 是否混淆
   */
  public TianaiOptions(String backgroundFormatName, String templateFormatName, Boolean obfuscate) {
    this.backgroundFormatName = backgroundFormatName;
    this.templateFormatName = templateFormatName;
    this.obfuscate = obfuscate;
  }

  /**
   * 将公共配置合并到当前配置，当前配置中为空的属性（null 或空白字符串）使用公共配置的值填充。
   *
   * <p>合并会直接修改当前实例。
   *
   * @param commonOptions 公共配置选项
   */
  public void merge(TianaiOptions commonOptions) {
    if (StringUtils.isBlank(backgroundFormatName)) {
      backgroundFormatName = commonOptions.backgroundFormatName;
    }
    if (StringUtils.isBlank(templateFormatName)) {
      templateFormatName = commonOptions.templateFormatName;
    }
    if (obfuscate == null) {
      obfuscate = commonOptions.obfuscate;
    }
    if (StringUtils.isBlank(backgroundImageTag)) {
      backgroundImageTag = commonOptions.backgroundImageTag;
    }
    if (StringUtils.isBlank(templateImageTag)) {
      templateImageTag = commonOptions.templateImageTag;
    }
    if (StringUtils.isBlank(fontTag)) {
      fontTag = commonOptions.fontTag;
    }
    if (tolerant == null) {
      tolerant = commonOptions.tolerant;
    }
    if (clickInterferenceCount == null) {
      clickInterferenceCount = commonOptions.clickInterferenceCount;
    }
    if (clickCheckClickCount == null) {
      clickCheckClickCount = commonOptions.clickCheckClickCount;
    }
  }
}
