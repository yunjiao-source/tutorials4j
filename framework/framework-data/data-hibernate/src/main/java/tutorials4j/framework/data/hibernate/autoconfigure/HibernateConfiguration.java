package tutorials4j.framework.data.hibernate.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Hibernate配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableJpaAuditing
public class HibernateConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("[DATA-HIBERNATE] Data Hibernate Configuration");
    }

}
