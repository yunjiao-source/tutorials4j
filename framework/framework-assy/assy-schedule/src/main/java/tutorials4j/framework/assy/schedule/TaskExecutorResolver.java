package tutorials4j.framework.assy.schedule;

import java.lang.reflect.Method;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class TaskExecutorResolver {
  private final ApplicationContext context;

  public TaskExecutorResolver(ApplicationContext context) {
    this.context = context;
  }

  public Runnable resolve(TaskDefinition definition) {
    if ("BEAN_METHOD".equalsIgnoreCase(definition.getTaskType())) {
      // taskData 格式："beanName:methodName"
      String[] parts = definition.getTaskData().split(":");
      Object bean = context.getBean(parts[0]);
      Method method = ReflectionUtils.findMethod(bean.getClass(), parts[1]);
      return () -> ReflectionUtils.invokeMethod(method, bean);
    } else if ("RUNNABLE_CLASS".equalsIgnoreCase(definition.getTaskType())) {
      // taskData 是全限定类名，需实现 Runnable
      try {
        Class<?> clazz = Class.forName(definition.getTaskData());
        return (Runnable) context.getBean(clazz); // 假设已注册为 Spring Bean
      } catch (Exception e) {
        throw new RuntimeException("Cannot instantiate Runnable task", e);
      }
    }
    throw new IllegalArgumentException("Unsupported task type");
  }
}
