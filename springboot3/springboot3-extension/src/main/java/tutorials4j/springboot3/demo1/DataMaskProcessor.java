package tutorials4j.springboot3.demo1;

import java.lang.reflect.Field;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

// 数据自动脱敏，适用于实体类、返回VO等所有场景
@Slf4j
@Component
public class DataMaskProcessor implements InstantiationAwareBeanPostProcessor {
  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    Field[] fields = bean.getClass().getDeclaredFields();
    for (Field field : fields) {
      if (field.isAnnotationPresent(DataMask.class)) {
        DataMask dataMask = field.getAnnotation(DataMask.class);
        field.setAccessible(true);
        try {
          String value = (String) field.get(bean);
          if (value == null || value.isEmpty()) {
            continue;
          }
          // 统一脱敏规则
          String maskValue =
              switch (dataMask.value()) {
                case PHONE -> value.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                case ID_CARD -> value.replaceAll("(\\d{6})\\d{8}(\\d{4})", "$1********$2");
              };
          field.set(bean, maskValue);
        } catch (Exception e) {
          log.error("数据脱敏失败，字段：{}", field.getName(), e);
        }
      }
    }
    return bean;
  }
}
