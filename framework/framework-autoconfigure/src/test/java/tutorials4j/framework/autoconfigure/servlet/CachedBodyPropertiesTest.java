package tutorials4j.framework.autoconfigure.servlet;

import jakarta.servlet.DispatcherType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.unit.DataSize;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * {@link CachedBodyProperties} 单元测试
 *
 * @author Yun Jiao
 */
@SpringBootTest()
public class CachedBodyPropertiesTest {
    @Autowired
    private CachedBodyProperties properties;

    @Test
    public void defaultValue() {
        assertThat(properties.getName()).isEqualTo("defaultCachedBodyFilter");
        assertThat(properties.getOrder()).isEqualTo(1);
        assertThat(properties.getDispatcherTypes()).isEqualTo(EnumSet.allOf(DispatcherType.class));
        assertThat(properties.getUrlPatterns()).isEqualTo(new String[]{"/*"});
        assertThat(properties.getMaxContentLength()).isEqualTo(DataSize.ofMegabytes(2));
    }


    @SpringBootConfiguration
    @EnableConfigurationProperties(CachedBodyProperties.class)
    static class Config {
    }
}
