package tutorials4j.framework.examples.simple;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.crypto.core.bean.DigestCategory;
import tutorials4j.framework.crypto.core.processor.DigestProcessor;
import tutorials4j.framework.crypto.core.processor.DigestProcessorFactory;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class DigestController {
  private final DigestProcessorFactory factory;

  @GetMapping("digest")
  public String digest(
      @RequestParam("category") DigestCategory category, @RequestParam("content") String content) {
    DigestProcessor service = factory.findProcessor(category);
    return service.digest(content);
  }
}
