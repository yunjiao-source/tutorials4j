package tutorials4j.framework.common.core.task;

import org.springframework.core.task.TaskDecorator;

import java.util.function.Supplier;

/**
 * {@link TaskDecorator} 实例提供者
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface TaskDecoratorSupplier extends Supplier<TaskDecorator> {
}
