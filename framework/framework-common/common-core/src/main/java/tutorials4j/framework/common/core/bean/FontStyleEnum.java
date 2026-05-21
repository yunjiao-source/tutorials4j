package tutorials4j.framework.common.core.bean;

import java.awt.Font;
import lombok.Getter;

/**
 * 字体风格
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
