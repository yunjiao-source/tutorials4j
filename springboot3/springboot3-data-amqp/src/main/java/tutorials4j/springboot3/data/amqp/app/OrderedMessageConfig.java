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
@Profile("orderedmessage")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.data.amqp.orderedmessage"})
@RequiredArgsConstructor
public class OrderedMessageConfig {}
