package tutorials4j.framework.data.mybatis.autoconfigure;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import tutorials4j.framework.data.core.properties.DataProperties;
import tutorials4j.framework.data.mybatis.InnerInterceptorSupplier;

/**
 * TODO
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
    MybatisPlusInterceptor mybatisPlusInterceptor(ObjectProvider<InnerInterceptorSupplier> innerInterceptorSuppliers) {
        log.debug("Tutorials4j - Data |- Mybatis Plus Interceptor");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        innerInterceptorSuppliers.orderedStream()
                .map(InnerInterceptorSupplier::get)
                .forEach(interceptor::addInnerInterceptor);
        return interceptor;
    }

    @Bean
    @Order(100)
    InnerInterceptorSupplier paginationInnerInterceptorSupplier(DataProperties properties) {
        log.debug("Tutorials4j - Data |- Pagination Inner Interceptor");
        return () -> new PaginationInnerInterceptor(properties.getMybatisPlus().getDbType());
    }

    @Bean
    @Order(200)
    InnerInterceptorSupplier optimisticLockerInnerInterceptorSupplier() {
        log.debug("Tutorials4j - Data |- Optimistic Locker Inner Interceptor");
        return OptimisticLockerInnerInterceptor::new;
    }

    @Bean
    @Order(300)
    InnerInterceptorSupplier blockAttackInnerInterceptorSupplier() {
        log.debug("Tutorials4j - Data |- Block Attack Inner Interceptor");
        return BlockAttackInnerInterceptor::new;
    }
}
