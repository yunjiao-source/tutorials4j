package tutorials4j.framework.data.mybatis;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import tutorials4j.framework.common.core.support.BeanCreator;

/**
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface InnerInterceptorCreator extends BeanCreator<InnerInterceptor> {
    @Override
    default Class<InnerInterceptor> getBeanClass() {
        return InnerInterceptor.class;
    }
}
