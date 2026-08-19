package tutorials4j.framework.data.mybatis.customizer;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.RequiredArgsConstructor;
import tutorials4j.framework.data.mybatis.MybatisPlusConsts;

/**
 * 分页拦截器自定义器。
 *
 * <p>向 MyBatis Plus 拦截器中添加 {@link PaginationInnerInterceptor}， 用于实现数据库物理分页功能，并指定数据库类型。
 *
 * @author Yun Jiao
 * @see PaginationInnerInterceptor
 */
@RequiredArgsConstructor
public class PaginationInnerInterceptorCustomizer implements MybatisPlusInterceptorCustomizer {
  private final DbType dbType;

  /**
   * 向拦截器中添加指定数据库类型的分页拦截器。
   *
   * @param interceptor MyBatis Plus 拦截器实例
   */
  @Override
  public void custom(MybatisPlusInterceptor interceptor) {
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));
  }

  /**
   * 返回分页拦截器的执行顺序。
   *
   * @return 拦截器顺序值
   */
  @Override
  public int getOrder() {
    return MybatisPlusConsts.INTERCEPTOR_ORDER_PAGINATION;
  }
}
