package tutorials4j.springboot3.web.apicrypto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.springboot3.common.jpa.UserDto;
import tutorials4j.springboot3.common.jpa.UserService;
import tutorials4j.springboot3.web.Result;
import tutorials4j.springboot3.web.apicrypto.config.Crypto;

/**
 * 用户控制器 测试加解密功能的示例接口
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/admin")
public class CryptoUserController {

  private final UserService userService;

  @PostMapping("/login")
  @Crypto // 标记接口需要加解密
  public Result<String> login(@RequestBody UserDto userInfo) {
    String token = userService.login(userInfo);
    return Result.success("登录成功", token);
  }
}
