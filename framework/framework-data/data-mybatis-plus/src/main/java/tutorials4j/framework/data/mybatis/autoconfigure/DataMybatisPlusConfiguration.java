package tutorials4j.framework.data.mybatis.autoconfigure;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import tutorials4j.framework.data.core.properties.DataProperties;
import tutorials4j.framework.data.mybatis.InnerInterceptorCreator;

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
    MybatisPlusInterceptor mybatisPlusInterceptor(ObjectProvider<InnerInterceptorCreator> innerInterceptorCreators) {
        log.debug("Tutorials4j - Data |- Mybatis Plus Interceptor");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        innerInterceptorCreators.orderedStream()
                .map(InnerInterceptorCreator::getInstance)
                .forEach(interceptor::addInnerInterceptor);
        return interceptor;
    }

    @Bean
    @Order(100)
    @ConditionalOnMissingBean
    InnerInterceptorCreator paginationInnerInterceptorCreator(DataProperties properties) {
        log.debug("Tutorials4j - Data |- Pagination Inner Interceptor Creator");
        return () -> new PaginationInnerInterceptor(properties.getMybatisPlus().getDbType());
    }

    @Bean
    @Order(200)
    @ConditionalOnMissingBean
    InnerInterceptorCreator optimisticLockerInnerInterceptorCreator() {
        log.debug("Tutorials4j - Data |- Optimistic Locker Inner Interceptor Creator");
        return OptimisticLockerInnerInterceptor::new;
    }

    @Bean
    @Order(300)
    @ConditionalOnMissingBean
    InnerInterceptorCreator blockAttackInnerInterceptorCreator() {
        log.debug("Tutorials4j - Data |- Block Attack Inner Interceptor Creator");
        return BlockAttackInnerInterceptor::new;
    }
}
