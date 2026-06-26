package tutorials4j.framework.examples.validation;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import tutorials4j.framework.common.core.bean.Result;

@Slf4j
@RestController
public class UserController {

  @PostMapping("/user/register")
  public Mono<Result<Void>> register(@Valid @RequestBody UserForm form) {
    // 如果校验通过，执行注册逻辑（此处模拟成功）
    log.info("注册用户：{}，邮箱：{}", form.getUsername(), form.getEmail());
    // 实际业务中可能保存用户、发送邮件等
    // 这里直接返回成功
    return Mono.just(Result.success());
  }
}
