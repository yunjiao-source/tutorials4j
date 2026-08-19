package tutorials4j.framework.common.core.bean;

import java.awt.Font;
import lombok.Getter;

/**
 * 字体风格枚举，映射 {@link java.awt.Font} 的样式常量（正常体、粗体、斜体）。
 *
 * <p>用于图片生成等场景中文本绘制的字体样式配置。
 *
 * @author Yun Jiao
 */
@Getter
public enum FontStyleEnum {
  /** 正常体 */
  plain(Font.PLAIN),

  /** 粗体 */
  bold(Font.BOLD),

  /** 斜体 */
  italic(Font.ITALIC);

  private final int mapping;

  FontStyleEnum(int mapping) {
    this.mapping = mapping;
  }
}
