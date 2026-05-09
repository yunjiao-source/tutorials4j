package tutorials4j.framework.data.mybatis.interceptor;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.RequiredArgsConstructor;
import tutorials4j.framework.data.mybatis.MybatisPlusConsts;
import tutorials4j.framework.data.mybatis.MybatisPlusInterceptorCustomizer;

/**
 * TODO
 *
 * @author Yun Jiao
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
