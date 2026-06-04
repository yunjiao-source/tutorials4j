package tutorials4j.springboot3.data.amqp.app;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("delayqueue")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.data.amqp.delayqueue"})
@RequiredArgsConstructor
public class DelayqueueConfig {}
