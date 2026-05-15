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

  @Override
  public void custom(MybatisPlusInterceptor interceptor) {
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));
  }

  @Override
  public int getOrder() {
    return MybatisPlusConsts.INTERCEPTOR_ORDER_PAGINATION;
  }
}
