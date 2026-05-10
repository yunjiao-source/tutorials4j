package tutorials4j.framework.tenant.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.RequiredArgsConstructor;
import tutorials4j.framework.data.mybatis.MybatisPlusConsts;
import tutorials4j.framework.data.mybatis.customizer.MybatisPlusInterceptorCustomizer;
import tutorials4j.framework.tenant.core.properties.TenantProperties;

import java.util.Set;

/**
 * 默认租户拦截器自定义器。
 * <p>
 * 向 MyBatis Plus 拦截器中添加 {@link TenantLineInnerInterceptor}，
 * 使用 {@link DefaultTenantLineHandler} 处理租户逻辑，从 {@link tutorials4j.framework.common.core.TenantContextHolder} 中获取当前租户 ID，
 * 并忽略配置中指定的表。
 * </p>
 *
 * @author Yun Jiao
 * @see TenantLineInnerInterceptor
 * @see DefaultTenantLineHandler
 */
@RequiredArgsConstructor
public class DefaultTenantLineInterceptorCustomizer implements MybatisPlusInterceptorCustomizer {
    private final TenantProperties properties;

    @Override
    public void custom(MybatisPlusInterceptor interceptor) {
        TenantLineHandler tenantLineHandler = new DefaultTenantLineHandler(Set.of(properties.getMybatisPlus().getIgnoreTable()));
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(tenantLineHandler));
    }

    @Override
    public int getOrder() {
        return MybatisPlusConsts.INTERCEPTOR_ORDER_TENANT;
    }
}
