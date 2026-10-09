package tutorials4j.toolkit.data.mybatisplus.interceptor;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;

/**
 * {@link MybatisPlusInterceptorCustomizer} 单元测试：接口默认契约。
 *
 * @author Yun Jiao
 */
class MybatisPlusInterceptorCustomizerTest {

  private static final MybatisPlusInterceptorCustomizer CUSTOMIZER = interceptor -> {};

  @Test
  @DisplayName("是函数式接口，可用 lambda 实现")
  void shouldBeFunctionalInterface() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

    CUSTOMIZER.custom(interceptor);

    assertThat(interceptor.getInterceptors()).isEmpty();
  }

  @Test
  @DisplayName("默认顺序为最低优先级")
  void shouldUseLowestPrecedenceByDefault() {
    assertThat(CUSTOMIZER.getOrder()).isEqualTo(Ordered.LOWEST_PRECEDENCE);
  }

  @Test
  @DisplayName("默认不声明拦截器类型")
  void shouldNotDeclareInterceptorClassByDefault() {
    assertThat(CUSTOMIZER.getInterceptorClass()).isNull();
  }

  @Test
  @DisplayName("实现 Ordered，可用于排序")
  void shouldImplementOrdered() {
    assertThat(CUSTOMIZER).isInstanceOf(Ordered.class);
  }

  @Test
  @DisplayName("自定义器可通过 lambda 向拦截器追加内部拦截器")
  void shouldAllowAddingInnerInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    InnerInterceptor innerInterceptor = new BlockAttackInnerInterceptor();

    MybatisPlusInterceptorCustomizer customizer =
        target -> target.addInnerInterceptor(innerInterceptor);
    customizer.custom(interceptor);

    assertThat(interceptor.getInterceptors()).containsExactly(innerInterceptor);
  }
}
