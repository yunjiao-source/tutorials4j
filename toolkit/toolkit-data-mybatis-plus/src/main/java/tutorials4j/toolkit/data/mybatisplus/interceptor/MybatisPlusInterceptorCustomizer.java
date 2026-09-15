package tutorials4j.toolkit.data.mybatisplus.interceptor;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
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

  default Class<? extends InnerInterceptor> getInterceptorClass() {
    return null;
  }
}
