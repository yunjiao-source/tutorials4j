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
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/crypto")
@RequiredArgsConstructor
public class CryptoController {
  private final CryptoProcessorFactory factory;

  @GetMapping("encrypt")
  public String encrypt(
      @RequestParam("category") CryptoCategory category, @RequestParam("content") String content) {
    CryptoProcessor service = factory.findProcessor(category);
    return service.encrypt(content);
  }

  @GetMapping("decrypt")
  public String decrypt(
      @RequestParam("category") CryptoCategory category, @RequestParam("content") String content) {
    CryptoProcessor service = factory.findProcessor(category);
    return service.decrypt(content);
  }
}
