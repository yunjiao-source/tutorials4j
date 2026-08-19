package tutorials4j.framework.common.spring.core;

import org.springframework.core.task.TaskDecorator;
import tutorials4j.framework.common.core.support.BeanCreator;

/**
 * 任务装饰器创建器接口。
 *
 * <p>由各具体实现提供 {@link TaskDecorator} 实例，供 {@link CompositeTaskDecoratorCreator} 组合使用。
 *
 * @author Yun Jiao
 * @see TaskDecorator
 * @see org.springframework.core.task.support.CompositeTaskDecorator
 */
public interface TaskDecoratorCreator extends BeanCreator<TaskDecorator> {
  /**
   * 返回任务装饰器的 Bean 类型。
   *
   * @return {@link TaskDecorator} 类型
   */
  @Override
  default Class<TaskDecorator> getBeanClass() {
    return TaskDecorator.class;
  }
}
