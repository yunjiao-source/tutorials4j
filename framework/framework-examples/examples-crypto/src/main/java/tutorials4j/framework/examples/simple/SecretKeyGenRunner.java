package tutorials4j.framework.examples.simple;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tutorials4j.framework.crypto.core.processor.CryptoProcessorFactory;
import tutorials4j.framework.crypto.core.processor.DigestProcessorFactory;

/**
 * 生成密钥
 *
 * @author Yun Jiao
 */
@Component
public class SecretKeyGenRunner implements CommandLineRunner {

  @Override
  public void run(String... args) throws Exception {
    System.out.println("--加解密工具---");
    CryptoProcessorFactory.instance
        .getProcessors()
        .forEach(
            (k, v) -> {
              System.out.println(">>>" + k);
              System.out.println(v.getSecretKey());
            });

    System.out.println("--摘要工具---");
    DigestProcessorFactory.instance
        .getProcessors()
        .forEach(
            (k, v) -> {
              System.out.println(">>>" + k);
              System.out.println(v.getSecretKey());
            });
  }
}
