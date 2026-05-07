package tutorials4j.framework.data.mybatis;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;

import java.util.function.Supplier;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface InnerInterceptorSupplier extends Supplier<InnerInterceptor> {
}
