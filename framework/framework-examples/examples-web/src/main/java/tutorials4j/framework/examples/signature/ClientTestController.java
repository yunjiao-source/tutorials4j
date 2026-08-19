package tutorials4j.framework.examples.signature;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 签名示例的测试控制器。
 *
 * <p>提供一组用于演示 {@link SignatureClient} 调用签名接口的 REST 接口。
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequestMapping("signature")
@RequiredArgsConstructor
public class ClientTestController {
  private final SignatureClient signatureClient;
}
