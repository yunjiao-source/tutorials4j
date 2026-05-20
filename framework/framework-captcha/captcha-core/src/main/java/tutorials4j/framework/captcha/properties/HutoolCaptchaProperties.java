package tutorials4j.framework.captcha.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.captcha.CodeGeneratorType;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.support.ColorTypeEnum;
import tutorials4j.framework.common.core.support.FontNameEnum;
import tutorials4j.framework.common.core.support.FontStyleEnum;

/**
 * Hutool 属性配置
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CAPTCHA_HUTOOL)
public class HutoolCaptchaProperties {

  /** 线段干扰的验证码 */
  @NestedConfigurationProperty
  private DrawingOptions line =
      DrawingOptions.of(
          new DrawingOptions(),
          250,
          50,
          60,
          ColorTypeEnum.white,
          null,
          null,
          true,
          FontNameEnum.Default,
          FontStyleEnum.plain,
          36,
          CodeGeneratorType.numAndChar,
          5);

  /** 圆圈干扰验证码 */
  @NestedConfigurationProperty
  private DrawingOptions circle =
      DrawingOptions.of(
          new DrawingOptions(),
          250,
          50,
          15,
          ColorTypeEnum.white,
          null,
          null,
          true,
          FontNameEnum.Default,
          FontStyleEnum.plain,
          36,
          CodeGeneratorType.numAndChar,
          5);

  /** 扭曲干扰验证码 */
  @NestedConfigurationProperty
  private DrawingOptions shear =
      DrawingOptions.of(
          new DrawingOptions(),
          250,
          50,
          8,
          ColorTypeEnum.white,
          null,
          null,
          true,
          FontNameEnum.Default,
          FontStyleEnum.plain,
          36,
          CodeGeneratorType.numAndChar,
          5);

  /** Gif验证码 */
  @NestedConfigurationProperty
  private GifDrawingOptions gif =
      GifDrawingOptions.of(
          new GifDrawingOptions(),
          250,
          50,
          10,
          ColorTypeEnum.white,
          null,
          null,
          true,
          FontNameEnum.Default,
          FontStyleEnum.plain,
          36,
          CodeGeneratorType.numAndChar,
          5,
          10,
          0,
          0,
          255);

  /** gif绘图配置 */
  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class GifDrawingOptions extends DrawingOptions {

    /** 量化器取样间隔, 1 ~ 20 值之间 - 默认是10ms */
    private Integer quality;

    /** 帧循环次数，默认是 0， 意味着无限循环 */
    private Integer repeat;

    /** 设置随机颜色时，最小的取色范围 */
    private Integer minColor;

    /** 设置随机颜色时，最大的取色范围 */
    private Integer maxColor;

    public static GifDrawingOptions of(
        GifDrawingOptions options,
        int width,
        int height,
        int interfereCount,
        ColorTypeEnum backgroundColor,
        Float transparency,
        Integer fuzziness,
        Boolean validIgnoreCase,
        FontNameEnum fontName,
        FontStyleEnum fontStyleEnum,
        Integer fontSize,
        CodeGeneratorType generator,
        int length,
        Integer quality,
        Integer repeat,
        Integer minColor,
        Integer maxColor) {
      DrawingOptions.of(
          options,
          width,
          height,
          interfereCount,
          backgroundColor,
          transparency,
          fuzziness,
          validIgnoreCase,
          fontName,
          fontStyleEnum,
          fontSize,
          generator,
          length);
      options.setQuality(quality);
      options.setRepeat(repeat);
      options.setMinColor(minColor);
      options.setMaxColor(maxColor);
      return options;
    }
  }

  /** 绘图配置 */
  @Data
  public static class DrawingOptions {

    /** 图片的宽度 */
    private Integer width;

    /** 图片的高度 */
    private Integer height;

    /** 验证码干扰元素个数（干扰线宽度） */
    private Integer interfereCount;

    /** 背景色 */
    private ColorTypeEnum backgroundColor;

    /** 文字透明度，取值0~1，1表示不透明 */
    private Float transparency;

    /** 模糊度（0 - 30） */
    private Integer fuzziness;

    /** 校验时忽略大小写 */
    private Boolean validIgnoreCase;

    /** 字体选项 */
    @NestedConfigurationProperty private FontOptions font;

    /** 码选项 */
    @NestedConfigurationProperty private CodeOptions code;

    public static DrawingOptions of(
        DrawingOptions options,
        int width,
        int height,
        int interfereCount,
        ColorTypeEnum backgroundColor,
        Float transparency,
        Integer fuzziness,
        Boolean validIgnoreCase,
        FontNameEnum fontName,
        FontStyleEnum fontStyleEnum,
        Integer fontSize,
        CodeGeneratorType generator,
        int length) {
      FontOptions font = FontOptions.of(new FontOptions(), fontName, fontStyleEnum, fontSize);
      CodeOptions code = CodeOptions.of(new CodeOptions(), generator, length);

      options.setWidth(width);
      options.setHeight(height);
      options.setInterfereCount(interfereCount);
      options.setBackgroundColor(backgroundColor);
      options.setTransparency(transparency);
      options.setFuzziness(fuzziness);
      options.setValidIgnoreCase(validIgnoreCase);

      options.setFont(font);
      options.setCode(code);
      return options;
    }
  }

  /** 字体选项 */
  @Data
  public static class FontOptions {

    /** 字体名称, 系统默认字体 */
    private FontNameEnum name = FontNameEnum.Default;

    /** 字体风格 */
    private FontStyleEnum style;

    /** 字体大小 */
    private Integer size;

    public static FontOptions of(
        FontOptions options, FontNameEnum name, FontStyleEnum style, Integer size) {
      options.setName(name);
      options.setStyle(style);
      options.setSize(size);
      return options;
    }
  }

  /** 码选项 */
  @Data
  public static class CodeOptions {

    /** 码生成类型 */
    private CodeGeneratorType generator;

    /** 字符长度 */
    private Integer length;

    public static CodeOptions of(CodeOptions options, CodeGeneratorType generator, int length) {
      options.setGenerator(generator);
      options.setLength(length);
      return options;
    }
  }
}
