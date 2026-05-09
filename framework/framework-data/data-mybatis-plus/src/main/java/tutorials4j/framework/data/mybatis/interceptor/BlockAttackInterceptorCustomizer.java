package tutorials4j.framework.data.mybatis.interceptor;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import lombok.RequiredArgsConstructor;
import tutorials4j.framework.data.mybatis.MybatisPlusConsts;
import tutorials4j.framework.data.mybatis.MybatisPlusInterceptorCustomizer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class BlockAttackInterceptorCustomizer implements MybatisPlusInterceptorCustomizer {

    @Override
    public void custom(MybatisPlusInterceptor interceptor) {
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
    }

    @Override
    public int getOrder() {
        return MybatisPlusConsts.INTERCEPTOR_ORDER_BLOCK_ATTACK;
    }
}
