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
        log.debug("Tutorials4j |- Common Core Configuration");
    }

    /**
     * 如果Spring容器中存在唯一的{@link TaskDecorator}, 就会自动注入到 {@link org.springframework.boot.autoconfigure.task.TaskExecutorConfigurations.ThreadPoolTaskExecutorBuilderConfiguration} 中
     * @return 实例
     */
    @Bean
    @ConditionalOnMissingBean(TaskDecorator.class)
    CompositeTaskDecorator CompositeTaskDecorator(ObjectProvider<TaskDecoratorSupplier> taskDecoratorSuppliers) {
        log.debug("Tutorials4j |- Composite Task Decorator");
        List<TaskDecorator> decoratorList = taskDecoratorSuppliers
                .orderedStream()
                .map(TaskDecoratorSupplier::get)
                .collect(Collectors.toList());
        return new CompositeTaskDecorator(decoratorList);
    }
}
