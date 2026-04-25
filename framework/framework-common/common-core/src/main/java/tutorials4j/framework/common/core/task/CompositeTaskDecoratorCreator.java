package tutorials4j.framework.common.core.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.CompositeTaskDecorator;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 复合任务装饰器创建器。
 *
 * <p>该组件负责从 Spring 容器中收集所有 {@link TaskDecoratorSupplier} 类型的 Bean，
 * 获取其提供的 {@link TaskDecorator} 实例，并按顺序组合成一个 {@link CompositeTaskDecorator}。
 *
 * <p><b>顺序处理说明：</b><br>
 * 由于 {@link CompositeTaskDecorator} 的执行顺序是装饰器列表的正向顺序（第一个装饰器最外层），
 * 而通常我们希望较高优先级的装饰器（Order 值较小）更靠近执行任务的核心，因此需要对收集到的装饰器列表进行反转。
 * 具体而言，supplier 按照 {@link org.springframework.core.Ordered} 升序提供，经反转后，
 * 较高优先级的装饰器将位于组合列表的末尾，从而在任务执行时被更靠近核心逻辑调用。
 *
 * @author Yun Jiao
 * @see TaskDecoratorSupplier
 * @see CompositeTaskDecorator
 * @see org.springframework.core.task.TaskDecorator
 */
@Slf4j
@RequiredArgsConstructor
public class CompositeTaskDecoratorCreator implements Supplier<CompositeTaskDecorator> {
    private final ObjectProvider<TaskDecoratorSupplier> taskDecoratorSuppliers;

    /**
     * 创建并返回一个组合任务装饰器。
     *
     * <p>从容器中按顺序获取所有 {@link TaskDecoratorSupplier} 实例，调用其 {@code get()} 方法获得
     * {@link TaskDecorator} 列表，然后将列表反转后构建 {@link CompositeTaskDecorator}。
     *
     * @return 包含所有已注册任务装饰器的组合装饰器；如果没有注册任何装饰器，则返回一个空组合装饰器
     */
    @Override
    public CompositeTaskDecorator get() {
        List<TaskDecorator> taskDecorator = taskDecoratorSuppliers.orderedStream()
                .map(TaskDecoratorSupplier::get)
                // 注意：需要倒序
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        l -> { Collections.reverse(l); return l; }
                ));

        log.debug("Tutorials4j - Common |- 组合任务装饰器[CompositeTaskDecorator]中组合实例信息：{}", taskDecorator);
        return new CompositeTaskDecorator(taskDecorator);
    }
}
