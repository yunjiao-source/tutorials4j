package tutorials4j.framework.captcha.properties;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class TianaiOptions {

  private String backgroundFormatName;

  private String templateFormatName;

  private Boolean obfuscate;

  private String backgroundImageTag;

  private String templateImageTag;

  private String fontTag;

  private Float tolerant;

  private Integer clickInterferenceCount;

  private Integer clickCheckClickCount;

  public TianaiOptions() {}

  public TianaiOptions(String backgroundFormatName, String templateFormatName, Boolean obfuscate) {
    this.backgroundFormatName = backgroundFormatName;
    this.templateFormatName = templateFormatName;
    this.obfuscate = obfuscate;
  }

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
