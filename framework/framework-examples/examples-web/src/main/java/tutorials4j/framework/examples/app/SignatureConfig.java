package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 接口签名校验示例模块的配置类，仅在 signature profile 下生效，负责扫描签名校验示例相关组件。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
@Profile("signature")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.signature"})
public class SignatureConfig {}
