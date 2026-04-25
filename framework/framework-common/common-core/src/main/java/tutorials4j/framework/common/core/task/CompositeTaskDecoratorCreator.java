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
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class CompositeTaskDecoratorCreator implements Supplier<CompositeTaskDecorator> {
    private final ObjectProvider<TaskDecoratorSupplier> taskDecoratorSuppliers;

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
