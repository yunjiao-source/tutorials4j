package tutorials4j.framework.data.mybatis;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;

import java.util.function.Supplier;

/**
 * 提供 MyBatis Plus 内部拦截器（{@link InnerInterceptor}）的函数式接口。
 *
 * <p>该接口继承自 {@link Supplier}，通常用于向 {@code MybatisPlusInterceptor} 中动态注册拦截器。
 * 在 Spring 配置中，可以通过声明该接口的 Bean 来向 MyBatis Plus 拦截器链中添加自定义或内置的拦截器。
 *
 * <p>使用示例：
 * <pre>{@code
 * @Bean
 * @Order(100)
 * public InnerInterceptorSupplier paginationInterceptorSupplier() {
 *     return () -> new PaginationInnerInterceptor(DbType.MYSQL);
 * }
 * }</pre>
 *
 * @author Yun Jiao
 * @see InnerInterceptor
 * @see com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
 * @since 1.0.0
 */
@FunctionalInterface
public interface InnerInterceptorSupplier extends Supplier<InnerInterceptor> {
}
