package tutorials4j.toolkit.core.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * {@link YesNoEnum} 单元测试。
 *
 * @author YunJiao Yang
 */
class YesNoEnumTest {

  @Test
  @DisplayName("枚举项为 yes / no")
  void shouldExposeTwoValues() {
    assertThat(Arrays.stream(YesNoEnum.values()).map(Enum::name)).containsExactly("no", "yes");
  }

  @Test
  @DisplayName("isYes 仅对 yes 返回 true")
  void shouldIdentifyYes() {
    assertThat(YesNoEnum.yes.isTrue()).isTrue();
    assertThat(YesNoEnum.no.isTrue()).isFalse();
  }

  @Test
  @DisplayName("isNo 仅对 no 返回 true")
  void shouldIdentifyNo() {
    assertThat(YesNoEnum.no.isFalse()).isTrue();
    assertThat(YesNoEnum.yes.isFalse()).isFalse();
  }

  @Test
  @DisplayName("isYes 与 isNo 互斥")
  void shouldBeMutuallyExclusive() {
    for (YesNoEnum value : YesNoEnum.values()) {
      assertThat(value.isTrue()).isNotEqualTo(value.isFalse());
    }
  }

  @Test
  @DisplayName("valueOf 按名称解析，未知名称抛异常")
  void shouldResolveByName() {
    assertThat(YesNoEnum.valueOf("yes")).isSameAs(YesNoEnum.yes);
    assertThatThrownBy(() -> YesNoEnum.valueOf("YES")).isInstanceOf(IllegalArgumentException.class);
  }
}
