package tutorials4j.toolkit.core.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link BeanCreator} 单元测试：函数式接口与默认实现契约。
 *
 * @author YunJiao Yang
 */
class BeanCreatorTest {

  @Test
  @DisplayName("可作为函数式接口使用（仅 getInstance 为抽象方法）")
  void shouldBeUsedAsLambda() {
    BeanCreator<String> creator = () -> "instance";

    assertThat(creator.getInstance()).isEqualTo("instance");
  }

  @Test
  @DisplayName("未重写时 newInstance 抛出 UnsupportedOperationException")
  void shouldRejectNewInstanceByDefault() {
    BeanCreator<String> creator = () -> "instance";

    assertThatThrownBy(creator::newInstance)
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessage("newInstance");
  }

  @Test
  @DisplayName("未重写时 getBeanClass 抛出 UnsupportedOperationException")
  void shouldRejectGetBeanClassByDefault() {
    BeanCreator<String> creator = () -> "instance";

    assertThatThrownBy(creator::getBeanClass)
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessage("getBeanClass");
  }

  @Test
  @DisplayName("重写默认方法后可返回新实例与 Bean 类型")
  void shouldSupportOverridingDefaults() {
    BeanCreator<StringBuilder> creator =
        new BeanCreator<>() {
          @Override
          public StringBuilder getInstance() {
            return new StringBuilder("cached");
          }

          @Override
          public StringBuilder newInstance() {
            return new StringBuilder("new");
          }

          @Override
          public Class<StringBuilder> getBeanClass() {
            return StringBuilder.class;
          }
        };

    assertThat(creator.getInstance()).hasToString("cached");
    assertThat(creator.newInstance()).hasToString("new");
    assertThat(creator.newInstance()).isNotSameAs(creator.getInstance());
    assertThat(creator.getBeanClass()).isEqualTo(StringBuilder.class);
  }
}
