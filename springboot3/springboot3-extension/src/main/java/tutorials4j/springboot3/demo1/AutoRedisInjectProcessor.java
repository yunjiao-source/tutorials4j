package tutorials4j.springboot3.demo1;

import java.lang.reflect.Field;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

// 全局自定义后置处理器
@Slf4j
@Component
public class AutoRedisInjectProcessor
    implements InstantiationAwareBeanPostProcessor, ApplicationContextAware {
  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
    Field[] fields = bean.getClass().getDeclaredFields();
    for (Field field : fields) {
      if (field.isAnnotationPresent(AutoRedis.class)) {
        log.info(">>>自动租入Redis");
        field.setAccessible(true);
        RedisUtil redisUtil = applicationContext.getBean(RedisUtil.class);
        try {
          field.set(bean, redisUtil);
        } catch (IllegalAccessException e) {
          throw new FatalBeanException("自动注入Redis资源失败", e);
        }
      }
    }
    return pvs;
  }
}
