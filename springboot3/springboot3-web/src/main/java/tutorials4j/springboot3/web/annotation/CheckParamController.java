package tutorials4j.springboot3.web.annotation;

import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CheckParam 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("check-param")
public class CheckParamController {

  @PostMapping("/user/save")
  public String saveUser(@RequestBody UserDTO userDTO) {
    return "用户保存成功：" + userDTO.getUserName();
  }

  @Data
  public static class UserDTO {
    @CheckParam(notNull = true, message = "用户ID")
    private String userId;

    @CheckParam(notNull = true, minLength = 2, maxLength = 10, message = "用户名")
    private String userName;

    @CheckParam(minLength = 11, maxLength = 11, message = "手机号")
    private String phone;

    private Integer age;
  }
}
