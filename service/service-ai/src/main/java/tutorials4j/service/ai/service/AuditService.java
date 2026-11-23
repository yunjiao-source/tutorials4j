package tutorials4j.service.ai.service;

import org.springframework.stereotype.Service;
import tutorials4j.service.ai.util.Utils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
public class AuditService {

  public void recordIntent(String name, String arguments) {
    Utils.greenSystemOut("审计到工具调用: " + name + ", 参数: " + arguments);
  }
}
