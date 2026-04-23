package tutorials4j.framework.common.core.autoconfigure;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.CompositeTaskDecorator;
import tutorials4j.framework.common.core.task.TaskDecoratorSupplier;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 核心配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@Import({SpringUtil.class})
public class CommonCoreConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Common - Core |- Common Core Configuration");
    }

    /**
     /**
     * 创建一个组合任务装饰器 {@link CompositeTaskDecorator}。
     *
     * <p>{@code CompositeTaskDecorator} 是 Spring 内置的装饰器组合器，会按照装饰器列表的顺序依次调用每个
     * {@link TaskDecorator} 的 {@code decorate} 方法，形成责任链效果。
     *
     * <p>该 Bean 会自动注入到默认的 {@link org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor}
     * 执行器中，用于修饰提交的任务（例如传递线程上下文、MDC 信息等）。
     *
     * <p>该方法会从容器中获取所有 {@link TaskDecoratorSupplier} 类型的 Bean，并通过
     * {@link ObjectProvider#orderedStream()} 保证按顺序获取（支持 {@link org.springframework.core.Ordered}
     * 或 {@link org.springframework.core.annotation.Order}）。然后调用每个 Supplier 的 {@code get()}
     * 方法获得实际的 {@link TaskDecorator} 实例，最后组装成 {@code CompositeTaskDecorator}。
     *
     * <p>仅当容器中不存在任何 {@link TaskDecorator} 类型的 Bean 时，该自动配置才会生效。
     *
     * @param taskDecoratorSuppliers 所有实现了 {@link TaskDecoratorSupplier} 接口的 Bean 提供者
     * @return 按顺序组合后的 {@code CompositeTaskDecorator} 实例
     */
    @Bean
    @ConditionalOnMissingBean(TaskDecorator.class)
    CompositeTaskDecorator CompositeTaskDecorator(ObjectProvider<TaskDecoratorSupplier> taskDecoratorSuppliers) {
        log.debug("Tutorials4j - Common - Core |- Composite Task Decorator");
        List<TaskDecorator> decoratorList = taskDecoratorSuppliers
                .orderedStream()
                .map(TaskDecoratorSupplier::get)
                .collect(Collectors.toList());
        return new CompositeTaskDecorator(decoratorList);
    }


}
