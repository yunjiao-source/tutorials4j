package tutorials4j.framework.data.mybatis.interceptor;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import lombok.RequiredArgsConstructor;
import tutorials4j.framework.data.mybatis.MybatisPlusConsts;
import tutorials4j.framework.data.mybatis.MybatisPlusInterceptorCustomizer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class OptimisticLockerInterceptorCustomizer implements MybatisPlusInterceptorCustomizer {

    @Override
    public void custom(MybatisPlusInterceptor interceptor) {
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
    }

    @Override
    public int getOrder() {
        return MybatisPlusConsts.INTERCEPTOR_ORDER_OPTIMISTIC_LOCKER;
    }
}
