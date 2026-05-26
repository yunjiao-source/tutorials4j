package tutorials4j.springboot3.web.messageconvert;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.springboot3.web.messageconvert.convert.User;
import tutorials4j.springboot3.web.messageconvert.convert.UserFormat;

/**
 * 用户接口
 *
 * @author Yun Jiao
 */
@RestController
public class UserController {
  @GetMapping("/user")
  public User getMappingUser(@UserFormat User user) {
    return user;
  }

  @PostMapping("/user")
  public User postMappingUser(@RequestParam("user") @UserFormat User user) {
    return user;
  }

  @GetMapping("/user-wrapper")
  public UserWrapper getMappingWrapper(UserWrapper wrapper) {
    return wrapper;
  }

  @PostMapping("/user-wrapper")
  public UserWrapper postMappingWrapper(@ModelAttribute UserWrapper wrapper) {
    return wrapper;
  }
}
