package tutorials4j.framework.common.core.bean;

import java.awt.Font;
import lombok.Getter;
import tutorials4j.framework.common.core.support.FontCache;

/**
 * 字体名称
 *
 * @author Yun Jiao
 */
@Getter
public enum FontNameEnum {

  // 阿里巴巴普惠体 3.0 (下载地址：<a href="https://www.alibabafonts.com/#/font">...</a>)
  /** 常规体 (字重：55) 最标准、最常用的字重。笔画清晰，可读性极高，是长时间阅读正文的首选，也被广泛用于用户界面（UI）的默认文本。 */
  AlibabaPuHuiTi355Regular("阿里巴巴普惠体 3.0 55 Regular"),

  /** 细体 / 纤体 (字重：35) 笔画最细，显得非常清秀、轻盈、现代。适用于大字号标题、时尚设计等需要精致感的场景，小字号下可能不易阅读。 */
  AlibabaPuHuiTi335Thin("阿里巴巴普惠体 3.0 35 Thin"),

  /** 轻体 (字重：45) 比Thin稍粗一点，但仍然保持轻盈的感觉。阅读起来比Thin更舒适，适合副标题、正文等。 */
  AlibabaPuHuiTi345Light("阿里巴巴普惠体 3.0 45 Light"),

  /**
   * 常规体（L3版本） (字重：55) 重与Regular完全相同（都是55）。后缀的 L3 很可能表示这是一个专门为西文（拉丁字母、数字、符号）优化的版本，或者是一个仅包含西文字符的子集字体，
   * 以确保在西文环境下显示效果更佳。中文部分可能保持不变或未包含。
   */
  AlibabaPuHuiTi355RegularL3("阿里巴巴普惠体 3.0 55 Regular L3"),

  /** 中等 (字重：65) 比Regular更粗一点，更有力量感。常用于需要稍加强调的小标题、按钮文字、数据标注等，在不使用粗体的情况下提供层次感。 */
  AlibabaPuHuiTi365Medium("阿里巴巴普惠体 3.0 65 Medium"),

  /** 半粗体 (字重：75) 已经具有明显的粗壮感。非常适合用于标题、强调性段落，视觉效果突出。 */
  AlibabaPuHuiTi375SemiBold("阿里巴巴普惠体 3.0 75 SemiBold"),

  /** 粗体 (字重：85) 标准的粗体，强调效果强烈。常用于重点标题、需要用户特别注意的提示信息等。 */
  AlibabaPuHuiTi385Bold("阿里巴巴普惠体 3.0 85 Bold"),

  /** 特粗体 / 超粗体 (字重：95) 非常粗壮，视觉冲击力极强。通常用于超大字号的主标题、海报、广告牌等，能够有效吸引眼球。 */
  AlibabaPuHuiTi395ExtraBold("阿里巴巴普惠体 3.0 95 ExtraBold"),

  /** 重体 / 黑体 Heavy (字重：105) 笔画极粗，几乎填满了所有空间。是字体家族中最粗的成员，用于需要最强视觉重量的场合，使用时要非常谨慎，以免显得臃肿。 */
  AlibabaPuHuiTi3105Heavy("阿里巴巴普惠体 3.0 105 Heavy"),

  /** 黑体 (字重：115) */
  AlibabaPuHuiTi3115Black("阿里巴巴普惠体 3.0 115 Black"),

  /** 思源黑体 (下载地址：<a href="https://github.com/adobe-fonts/source-han-sans/releases">...</a>) */
  SourceHanSansVF("Source Han Sans VF"),

  /** 思源宋体 (下载地址：<a href="https://github.com/adobe-fonts/source-han-serif/releases">...</a>) */
  SourceHanSerifVF("Source Han Serif VF"),

  /** 通用字体：宋体 */
  SimSun("宋体"),

  /** 通用字体：新宋体 */
  NSimSun("新宋体"),

  /** 默认系统字体 */
  Default("SansSerif");

  private final String name;

  FontNameEnum(String name) {
    this.name = name;
  }

  /**
   * 获取字体
   *
   * @param style 字体样式 (Font.PLAIN, Font.BOLD, Font.ITALIC)
   * @param size 字体大小
   * @return 请求的字体
   */
  public Font getFont(int style, int size) {
    return FontCache.getInstance().getFont(getName(), style, size);
  }

  /**
   * 获取字体 (默认样式和大小)
   *
   * @return 请求的字体
   */
  public Font getFont() {
    return FontCache.getInstance().getFont(getName());
  }
}
