package tutorials4j.springboot3.web.validation;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请求体参数校验
 *
 * @author yangyunjiao
 */
@RestController
@RequestMapping("valid")
public class ValidController {
  @PostMapping("check")
  public User checkBodyParam(@RequestBody @Valid User user) {
    return user;
  }

  @PostMapping("check-multilevel")
  public UserClass checkBodyMultilevelParam(@RequestBody @Valid UserClass userClass) {
    return userClass;
  }
}
