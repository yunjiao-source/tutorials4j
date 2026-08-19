package tutorials4j.framework.examples.validation;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import tutorials4j.framework.common.core.bean.Result;

/**
 * 用户注册控制器
 *
 * <p>演示基于 {@code @Valid} + {@code @RequestBody} 的参数校验，校验通过后模拟执行注册逻辑。
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
public class UserController {

  /**
   * 用户注册接口
   *
   * @param form 用户注册表单，经校验通过后执行注册
   * @return 注册结果
   */
  @PostMapping("/user/register")
  public Mono<Result<Void>> register(@Valid @RequestBody UserForm form) {
    // 如果校验通过，执行注册逻辑（此处模拟成功）
    log.info("注册用户：{}，邮箱：{}", form.getUsername(), form.getEmail());
    // 实际业务中可能保存用户、发送邮件等
    // 这里直接返回成功
    return Mono.just(Result.success());
  }
}
