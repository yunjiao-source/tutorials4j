package tutorials4j.framework.common.core.task;

import org.springframework.core.task.TaskDecorator;

import java.util.function.Supplier;

/**
 * 提供 {@link TaskDecorator} 实例的函数式接口。
 *
 * <p>每个业务模块可以通过实现该接口并声明为 Spring Bean，或直接通过 {@code @Bean} 方法返回自定义的
 * {@link TaskDecorator} 实现。框架会自动收集所有 {@code TaskDecoratorSupplier} 类型的 Bean，
 * 并将其包装为 {@link org.springframework.core.task.support.CompositeTaskDecorator} 应用于异步任务执行。
 *
 * <p>实现类可通过实现 {@link org.springframework.core.Ordered} 接口或使用
 * {@link org.springframework.core.annotation.Order} 注解来控制装饰器的执行顺序。
 *
 * @author Yun Jiao
 * @see TaskDecorator
 * @see org.springframework.core.task.support.CompositeTaskDecorator
 */
@FunctionalInterface
public interface TaskDecoratorSupplier extends Supplier<TaskDecorator> {
}
