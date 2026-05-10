package tutorials4j.framework.data.mybatis.customizer;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.core.Ordered;

/**
 * MyBatis Plus 拦截器自定义器接口。
 * <p>
 * 用于向 {@link MybatisPlusInterceptor} 中添加内部拦截器（如分页、乐观锁、租户等），
 * 并控制这些拦截器的执行顺序。
 * </p>
 *
 * @author Yun Jiao
 * @see Ordered
 */
@FunctionalInterface
public interface MybatisPlusInterceptorCustomizer extends Ordered {
    /**
     * 对 MyBatis Plus 拦截器执行自定义操作，通常用于添加内部拦截器。
     *
     * @param interceptor 待自定义的 MyBatis Plus 拦截器实例，不能为 {@code null}
     */
    void custom(MybatisPlusInterceptor interceptor);

    /**
     * 返回当前自定义器的顺序值，值越小优先级越高。
     * <p>默认返回最低优先级 {@link Ordered#LOWEST_PRECEDENCE}，子类应适当重写。</p>
     *
     * @return 顺序值，默认为 {@code Ordered.LOWEST_PRECEDENCE}
     */
    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
