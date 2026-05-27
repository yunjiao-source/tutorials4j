package tutorials4j.springboot3.web.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请求体参数校验
 *
 * <p>注意：@Validate注解可有可无，如果有触发ConstraintViolationException异常，没有触发HandlerMethodValidationException异常
 *
 * @author yangyunjiao
 */
@RestController
@RequestMapping("validated")
// @Validated
public class ValiddatedController {
  @PostMapping("check-list")
  public String checkList(@RequestBody @Valid List<User> users) {
    return "ok";
  }

  @PostMapping("check-valid-list")
  public String checkValidList(@RequestBody @Valid ValidList<User> users) {
    return "ok";
  }

  @GetMapping("check-param")
  public String checkParam(@RequestParam("age") @Max(value = 99, message = "不能大于99岁") Integer age) {
    return "ok";
  }

  @GetMapping("check-path/{id}")
  public String checkPath(
      @PathVariable("id") @Pattern(regexp = "^[0-9]*$", message = "id参数值必须是正整数") String id) {
    return "ok";
  }
}
