package tutorials4j.microservice.demo.controller;

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequestMapping("ip-authority")
public class IpAuthorityController {
  @GetMapping("get")
  public String get() {
    log.info(">>> ip-authority 被调用");
    return "ip-authority/get接口返回请求数据：" + Instant.now().toString();
  }
}
