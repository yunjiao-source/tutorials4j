package tutorials4j.framework.data.mybatis.autoconfigure;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.data.core.properties.DataMybatisPlusProperties;
import tutorials4j.framework.data.mybatis.AuditMetaObjectHandler;
import tutorials4j.framework.data.mybatis.DefaultIdentifierGenerator;
import tutorials4j.framework.data.mybatis.MybatisPlusInterceptorCustomizer;
import tutorials4j.framework.data.mybatis.interceptor.BlockAttackInterceptorCustomizer;
import tutorials4j.framework.data.mybatis.interceptor.OptimisticLockerInterceptorCustomizer;
import tutorials4j.framework.data.mybatis.interceptor.PaginationInnerInterceptorCustomizer;

/**
 * MyBatis Plus 的自动配置类
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class DataMybatisPlusConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Data |- Data Mybatis Plus Configuration");
    }

    @Bean
    @ConditionalOnMissingBean
    MybatisPlusInterceptor mybatisPlusInterceptor(ObjectProvider<MybatisPlusInterceptorCustomizer> customizers) {
        log.debug("Tutorials4j - Data |- Mybatis Plus Interceptor");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        customizers.orderedStream()
                .forEach(customizer -> customizer.custom(interceptor));
        return interceptor;
    }

    @Bean
    @ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_DATA_MYBATIS_PLUS, name = "interceptors.pagination", havingValue = "true", matchIfMissing = true)
    MybatisPlusInterceptorCustomizer paginationInnerInterceptorCustomizer(DataMybatisPlusProperties properties) {
        log.debug("Tutorials4j - Data |- Pagination Inner Interceptor Customizer");
        return new PaginationInnerInterceptorCustomizer(properties.getDbType());
    }

    @Bean
    @ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_DATA_MYBATIS_PLUS, name = "interceptors.optimistic-locker", havingValue = "true", matchIfMissing = true)
    MybatisPlusInterceptorCustomizer optimisticLockerInnerInterceptorCustomizer() {
        log.debug("Tutorials4j - Data |- Optimistic Locker Inner Interceptor Customizer");
        return new OptimisticLockerInterceptorCustomizer();
    }

    @Bean
    @ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_DATA_MYBATIS_PLUS, name = "interceptors.block-attack", havingValue = "true", matchIfMissing = true)
    MybatisPlusInterceptorCustomizer blockAttackInnerInterceptorCustomizer() {
        log.debug("Tutorials4j - Data |- Block Attack Inner Interceptor Customizer");
        return new BlockAttackInterceptorCustomizer();
    }

    @Bean
    MybatisPlusPropertiesCustomizer defaultIdentifierGeneratorMybatisPlusPropertiesCustomizer() {
        log.debug("Tutorials4j - Data |- Default Identifier Generator Mybatis Plus Properties Customizer");
        return plusProperties -> plusProperties.getGlobalConfig().setIdentifierGenerator(new DefaultIdentifierGenerator());
    }

    @Bean
    @ConditionalOnMissingBean
    MetaObjectHandler auditMetaObjectHandler() {
        log.debug("Tutorials4j - Data |- Audit Meta Object Handler");
        return new AuditMetaObjectHandler();
    }
}
