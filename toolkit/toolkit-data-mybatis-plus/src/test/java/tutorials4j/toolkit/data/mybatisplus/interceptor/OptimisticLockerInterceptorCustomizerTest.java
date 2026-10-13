package tutorials4j.toolkit.data.mybatisplus.interceptor;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link OptimisticLockerInterceptorCustomizer} 单元测试：乐观锁拦截器装配。
 *
 * @author YunJiao Yang
 */
class OptimisticLockerInterceptorCustomizerTest {
  private final OptimisticLockerInterceptorCustomizer customizer =
      new OptimisticLockerInterceptorCustomizer();

  @Test
  @DisplayName("添加乐观锁内部拦截器")
  void shouldAddOptimisticLockerInnerInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

    customizer.custom(interceptor);

    assertThat(interceptor.getInterceptors()).hasSize(1);
    assertThat(interceptor.getInterceptors().get(0))
        .isInstanceOf(OptimisticLockerInnerInterceptor.class);
  }

  @Test
  @DisplayName("执行顺序为 200，晚于分页、早于防全表攻击")
  void shouldRunAfterPaginationAndBeforeBlockAttack() {
    assertThat(customizer.getOrder()).isEqualTo(200);
    assertThat(customizer.getOrder())
        .isGreaterThan(new PaginationInnerInterceptorCustomizer(null).getOrder());
    assertThat(customizer.getOrder()).isLessThan(new BlockAttackInterceptorCustomizer().getOrder());
  }

  @Test
  @DisplayName("声明自身负责的拦截器类型")
  void shouldDeclareInterceptorClass() {
    assertThat(customizer.getInterceptorClass()).isEqualTo(OptimisticLockerInnerInterceptor.class);
  }
}
