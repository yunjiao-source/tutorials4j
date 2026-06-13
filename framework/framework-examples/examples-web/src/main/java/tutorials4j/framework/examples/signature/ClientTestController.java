package tutorials4j.framework.examples.signature;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
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
