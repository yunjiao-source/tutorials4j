package tutorials4j.springboot3.demo1;

// 全局服务动态代理，实现统一日志与性能监控

import java.util.Arrays;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class ServiceLogProxyProcessor implements BeanPostProcessor, Ordered {

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    // 精准拦截所有Service层Bean
    if (!bean.getClass().isAnnotationPresent(Service.class)) {
      return bean;
    }
    // CGLIB动态代理增强
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(bean.getClass());
    enhancer.setCallback(
        (MethodInterceptor)
            (obj, method, args, proxy) -> {
              long start = System.currentTimeMillis();
              System.out.println("[服务执行] 方法：" + method.getName() + "，入参：" + Arrays.toString(args));
              Object result;
              try {
                result = method.invoke(bean, args);
                System.out.println("[执行成功] 耗时：" + (System.currentTimeMillis() - start) + "ms");
                return result;
              } catch (Exception e) {
                System.err.println("[执行异常] 方法：" + method.getName() + "，异常信息：" + e.getMessage());
                throw e;
              }
            });
    return enhancer.create();
  }

  // 降低优先级，保证不覆盖Spring原生事务代理
  @Override
  public int getOrder() {
    return 100;
  }
}
