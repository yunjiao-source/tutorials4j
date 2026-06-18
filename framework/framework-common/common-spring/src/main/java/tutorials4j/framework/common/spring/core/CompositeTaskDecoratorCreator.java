package tutorials4j.framework.common.spring.core;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.CompositeTaskDecorator;
import tutorials4j.framework.common.core.support.BeanCreator;

/**
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class CompositeTaskDecoratorCreator implements BeanCreator<CompositeTaskDecorator> {
  private final List<TaskDecoratorCreator> taskDecoratorCreators;

  private CompositeTaskDecorator instance;

  /**
   * 创建并返回一个组合任务装饰器。
   *
   * <p>从容器中按顺序获取所有 {@link TaskDecoratorCreator} 实例，调用其 {@code get()} 方法获得 {@link TaskDecorator}
   * 列表，然后将列表反转后构建 {@link CompositeTaskDecorator}。
   *
   * @return 包含所有已注册任务装饰器的组合装饰器；如果没有注册任何装饰器，则返回一个空组合装饰器
   */
  @Override
  public CompositeTaskDecorator getInstance() {
    if (instance != null) {
      return instance;
    }

    synchronized (this) {
      if (instance != null) {
        return instance;
      }

      instance = newInstance();
    }
    return instance;
  }

  @Override
  public CompositeTaskDecorator newInstance() {
    List<TaskDecorator> taskDecorators =
        taskDecoratorCreators.stream()
            .map(TaskDecoratorCreator::getInstance)
            .collect(Collectors.toList());
    if (log.isDebugEnabled()) {
      log.debug(
          "TaskDecorator instances will be injected into the CompositeTaskDecorator instance, totaling {}",
          taskDecorators);
    }
    return new CompositeTaskDecorator(taskDecorators);
  }

  @Override
  public Class<CompositeTaskDecorator> getBeanClass() {
    return CompositeTaskDecorator.class;
  }
}
