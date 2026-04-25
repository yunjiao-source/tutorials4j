package tutorials4j.framework.common.core.autoconfigure;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.common.core.task.CompositeTaskDecoratorCreator;
import tutorials4j.framework.common.core.task.TaskDecoratorSupplier;

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
        log.debug("Tutorials4j - Common |- Common Core Configuration");
    }

    @Bean
    @ConditionalOnMissingBean
    CompositeTaskDecoratorCreator compositeTaskDecoratorCreator(ObjectProvider<TaskDecoratorSupplier> taskDecoratorSuppliers) {
        log.debug("Tutorials4j - Common |- Composite Task Decorator Creator");

        return new CompositeTaskDecoratorCreator(taskDecoratorSuppliers);
    }


}
