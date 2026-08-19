package tutorials4j.framework.common.core.bean;

import java.awt.Color;
import lombok.Getter;

/**
 * 颜色类型枚举，定义了常用颜色名称与 {@link java.awt.Color} 的映射关系。
 *
 * <p>常用于图片生成（如二维码）等场景的配色配置，通过枚举项即可获取对应的 AWT 颜色对象。
 *
 * @author Yun Jiao
 */
@Getter
public enum ColorTypeEnum {
  /** 白色 */
  white(Color.WHITE),

  /** 浅灰色 */
  lightGray(Color.LIGHT_GRAY),

  /** 灰色 */
  gray(Color.GRAY),

  /** 深灰色 */
  darkGray(Color.DARK_GRAY),

  /** 黑色 */
  black(Color.BLACK),

  /** 红色 */
  red(Color.RED),

  /** 粉红色 */
  pink(Color.PINK),

  /** 橘色 */
  orange(Color.ORANGE),

  /** 黄色 */
  yellow(Color.YELLOW),

  /** 绿色 */
  green(Color.GREEN),

  /** 洋红色 */
  magenta(Color.MAGENTA),

  /** 青色 */
  cyan(Color.CYAN),

  /** 蓝色 */
  blue(Color.BLUE);

  private final Color mapping;

  ColorTypeEnum(Color mapping) {
    this.mapping = mapping;
  }
}
