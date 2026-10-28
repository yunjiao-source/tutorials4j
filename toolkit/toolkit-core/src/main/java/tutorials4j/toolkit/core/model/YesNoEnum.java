package tutorials4j.toolkit.core.model;

import java.util.Objects;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public enum YesNoEnum {
  no,
  yes;

  public boolean isTrue() {
    return Objects.equals(this, yes);
  }

  public boolean isFalse() {
    return Objects.equals(this, no);
  }

  public int toInt() {
    return ordinal();
  }

  public static YesNoEnum formInt(int ordinal) {
    if (ordinal == 1) {
      return yes;
    } else if (ordinal == 0) {
      return no;
    }
    throw new IllegalArgumentException("Unknown YesNoEnum ordinal: " + ordinal);
  }

  public static boolean isTrue(YesNoEnum yesNoEnum) {
    return Objects.equals(YesNoEnum.yes, yesNoEnum);
  }

  public static boolean isFalse(YesNoEnum yesNoEnum) {
    return Objects.equals(YesNoEnum.no, yesNoEnum);
  }

  public static YesNoEnum valueOf(Boolean b) {
    if (b == null) {
      return null;
    }

    return b ? yes : no;
  }
}
