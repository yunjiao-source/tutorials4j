package tutorials4j.toolkit.core.model;

import java.util.Objects;

/**
 * TODO
 *
 * @author YunJiao Yang
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

  public int toInt() {
    return isYes() ? 1 : 0;
  }
}
