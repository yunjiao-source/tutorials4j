package tutorials4j.toolkit.data.mybatisplus.interceptor;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link BlockAttackInterceptorCustomizer} 单元测试：防全表更新/删除拦截器装配。
 *
 * @author Yun Jiao
 */
class BlockAttackInterceptorCustomizerTest {
  private final BlockAttackInterceptorCustomizer customizer =
      new BlockAttackInterceptorCustomizer();

  @Test
  @DisplayName("添加防全表更新/删除内部拦截器")
  void shouldAddBlockAttackInnerInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

    customizer.custom(interceptor);

    assertThat(interceptor.getInterceptors()).hasSize(1);
    assertThat(interceptor.getInterceptors().get(0))
        .isInstanceOf(BlockAttackInnerInterceptor.class);
  }

  @Test
  @DisplayName("执行顺序为 300，在三个内置自定义器中最后执行")
  void shouldRunLastAmongBuiltInCustomizers() {
    assertThat(customizer.getOrder()).isEqualTo(300);
    assertThat(customizer.getOrder())
        .isGreaterThan(new PaginationInnerInterceptorCustomizer(null).getOrder());
    assertThat(customizer.getOrder())
        .isGreaterThan(new OptimisticLockerInterceptorCustomizer().getOrder());
  }

  @Test
  @DisplayName("声明自身负责的拦截器类型")
  void shouldDeclareInterceptorClass() {
    assertThat(customizer.getInterceptorClass()).isEqualTo(BlockAttackInnerInterceptor.class);
  }
}
