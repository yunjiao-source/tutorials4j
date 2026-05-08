package tutorials4j.framework.common.core.task;

import org.springframework.core.task.TaskDecorator;
import tutorials4j.framework.common.core.support.BeanCreator;

/**
 *
 * @author Yun Jiao
 * @see TaskDecorator
 * @see org.springframework.core.task.support.CompositeTaskDecorator
 */
public interface TaskDecoratorCreator extends BeanCreator<TaskDecorator> {
    @Override
    default Class<TaskDecorator> getBeanClass() {
        return TaskDecorator.class;
    }
}
