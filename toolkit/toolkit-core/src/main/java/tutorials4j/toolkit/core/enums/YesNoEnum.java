package tutorials4j.toolkit.core.enums;

import java.util.Objects;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public enum YesNoEnum {
  yes,
  no;

  public boolean isYes() {
    return Objects.equals(this, yes);
  }

  public boolean isNo() {
    return Objects.equals(this, no);
  }
}
