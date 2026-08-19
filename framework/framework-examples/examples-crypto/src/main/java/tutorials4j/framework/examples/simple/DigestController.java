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
 * 摘要（散列）计算示例控制器。
 *
 * <p>提供基于 {@link DigestProcessorFactory} 的摘要计算 REST 接口，根据指定的 {@link DigestCategory} 动态查找对应的 {@link
 * DigestProcessor} 计算内容摘要。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class DigestController {
  private final DigestProcessorFactory factory;

  /**
   * 使用指定类别的摘要处理器计算内容摘要。
   *
   * @param category 摘要算法类别
   * @param content 待计算摘要的内容
   * @return 计算得到的摘要值
   */
  @GetMapping("digest")
  public String digest(
      @RequestParam("category") DigestCategory category, @RequestParam("content") String content) {
    DigestProcessor service = factory.findProcessor(category);
    return service.digest(content);
  }
}
