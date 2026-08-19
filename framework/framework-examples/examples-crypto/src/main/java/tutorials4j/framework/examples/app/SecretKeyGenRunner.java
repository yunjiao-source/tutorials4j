package tutorials4j.framework.examples.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tutorials4j.framework.crypto.core.processor.CryptoProcessorFactory;
import tutorials4j.framework.crypto.core.processor.DigestProcessorFactory;

/**
 * 密钥生成运行器。
 *
 * <p>应用启动后输出加解密处理器与摘要处理器各自的密钥，用于示例演示。
 *
 * @author Yun Jiao
 */
@Component
public class SecretKeyGenRunner implements CommandLineRunner {

  /**
   * 输出所有加解密处理器与摘要处理器的密钥。
   *
   * @param args 启动参数
   * @throws Exception 生成密钥失败时抛出
   */
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
