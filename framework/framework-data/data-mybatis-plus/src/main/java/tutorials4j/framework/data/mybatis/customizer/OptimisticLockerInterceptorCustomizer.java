package tutorials4j.framework.data.mybatis.customizer;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import tutorials4j.framework.data.mybatis.MybatisPlusConsts;

/**
 * 乐观锁拦截器自定义器。
 * <p>
 * 向 MyBatis Plus 拦截器中添加 {@link OptimisticLockerInnerInterceptor}，
 * 用于实现基于版本号的乐观锁机制。
 * </p>
 *
 * @author Yun Jiao
 * @see OptimisticLockerInnerInterceptor
 */
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
