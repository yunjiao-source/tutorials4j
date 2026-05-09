package tutorials4j.framework.data.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.core.Ordered;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface MybatisPlusInterceptorCustomizer extends Ordered {
    void custom(MybatisPlusInterceptor interceptor);

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
