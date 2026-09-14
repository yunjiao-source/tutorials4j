package tutorials4j.microservice.demo.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 参数校验示例控制器
 *
 * <p>演示 JSON 请求体、表单、请求参数三类常见校验失败场景（分别触发 {@code MethodArgumentNotValidException}、{@code
 * BindException}、{@code ConstraintViolationException}）， 以及访问不存在的静态资源触发的 {@code
 * NoResourceFoundException}。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("validation")
@Validated
public class ValidationController {

  // 1. 触发 MethodArgumentNotValidException（@RequestBody JSON 校验）
  /**
   * JSON 请求体校验示例
   *
   * @param request JSON 请求体
   * @return 校验通过提示
   */
  @PostMapping("json")
  public String testJson(@Valid @RequestBody JsonRequest request) {
    return "JSON 校验通过";
  }

  // 2. 触发 BindException（表单/@ModelAttribute 校验）
  /**
   * 表单校验示例
   *
   * @param request 表单请求
   * @return 校验通过提示
   */
  @PostMapping("form")
  public String testForm(@Valid @ModelAttribute FormRequest request) {
    return "表单校验通过";
  }

  // 3. 触发 ConstraintViolationException（@RequestParam 校验）
  /**
   * 请求参数校验示例
   *
   * @param name 请求参数，需满足非空且长度 2~10
   * @return 校验通过提示
   */
  @GetMapping("param")
  public String testParam(@RequestParam("name") @NotBlank @Size(min = 2, max = 10) String name) {
    return "参数校验通过";
  }

  @Data
  public static class JsonRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3~20之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6位")
    private String password;
  }

  @Data
  public static class FormRequest {
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 10, message = "姓名长度2~10位")
    private String name;

    @NotBlank(message = "邮箱不能为空")
    private String email;
  }
}
