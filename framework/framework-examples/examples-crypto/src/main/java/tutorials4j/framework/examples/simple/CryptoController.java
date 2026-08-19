package tutorials4j.framework.examples.simple;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.crypto.core.bean.CryptoCategory;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;
import tutorials4j.framework.crypto.core.processor.CryptoProcessorFactory;

/**
 * 加解密示例控制器。
 *
 * <p>提供基于 {@link CryptoProcessorFactory} 的加解密 REST 接口，根据指定的 {@link CryptoCategory} 动态查找对应的 {@link
 * CryptoProcessor} 完成内容的加密与解密。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/crypto")
@RequiredArgsConstructor
public class CryptoController {
  private final CryptoProcessorFactory factory;

  /**
   * 使用指定类别的加密处理器对内容进行加密。
   *
   * @param category 加密算法类别
   * @param content 待加密的内容
   * @return 加密后的密文
   */
  @GetMapping("encrypt")
  public String encrypt(
      @RequestParam("category") CryptoCategory category, @RequestParam("content") String content) {
    CryptoProcessor service = factory.findProcessor(category);
    return service.encrypt(content);
  }

  /**
   * 使用指定类别的加密处理器对内容进行解密。
   *
   * @param category 加密算法类别
   * @param content 待解密的内容
   * @return 解密后的明文
   */
  @GetMapping("decrypt")
  public String decrypt(
      @RequestParam("category") CryptoCategory category, @RequestParam("content") String content) {
    CryptoProcessor service = factory.findProcessor(category);
    return service.decrypt(content);
  }
}
