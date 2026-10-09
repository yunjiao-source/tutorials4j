package tutorials4j.toolkit.data.mybatisplus.interceptor;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link PaginationInnerInterceptorCustomizer} 单元测试：分页拦截器装配。
 *
 * @author Yun Jiao
 */
class PaginationInnerInterceptorCustomizerTest {

  @Test
  @DisplayName("按配置的数据库类型添加分页内部拦截器")
  void shouldAddPaginationInnerInterceptorWithDbType() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

    new PaginationInnerInterceptorCustomizer(DbType.POSTGRE_SQL).custom(interceptor);

    assertThat(interceptor.getInterceptors()).hasSize(1);
    assertThat(interceptor.getInterceptors().get(0)).isInstanceOf(PaginationInnerInterceptor.class);
    assertThat(((PaginationInnerInterceptor) interceptor.getInterceptors().get(0)).getDbType())
        .isEqualTo(DbType.POSTGRE_SQL);
  }

  @Test
  @DisplayName("执行顺序为 100，早于乐观锁与防全表攻击拦截器")
  void shouldRunFirstAmongBuiltInCustomizers() {
    PaginationInnerInterceptorCustomizer customizer =
        new PaginationInnerInterceptorCustomizer(DbType.MYSQL);

    assertThat(customizer.getOrder()).isEqualTo(100);
    assertThat(customizer.getOrder())
        .isLessThan(new OptimisticLockerInterceptorCustomizer().getOrder());
    assertThat(customizer.getOrder()).isLessThan(new BlockAttackInterceptorCustomizer().getOrder());
  }

  @Test
  @DisplayName("声明自身负责的拦截器类型")
  void shouldDeclareInterceptorClass() {
    assertThat(new PaginationInnerInterceptorCustomizer(DbType.MYSQL).getInterceptorClass())
        .isEqualTo(PaginationInnerInterceptor.class);
  }

  @Test
  @DisplayName("重复调用会重复添加（addInnerInterceptor 不去重）")
  void shouldAddInterceptorForEachCall() {
    PaginationInnerInterceptorCustomizer customizer =
        new PaginationInnerInterceptorCustomizer(DbType.MYSQL);
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

    customizer.custom(interceptor);
    customizer.custom(interceptor);

    assertThat(interceptor.getInterceptors()).hasSize(2);
  }
}
