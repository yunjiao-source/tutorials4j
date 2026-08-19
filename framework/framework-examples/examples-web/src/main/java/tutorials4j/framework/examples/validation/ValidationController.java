package tutorials4j.framework.examples.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import tutorials4j.framework.common.core.bean.Result;

/**
 * 参数校验示例控制器
 *
 * <p>演示 JSON 请求体、表单、请求参数三类常见校验失败场景（分别触发 {@code MethodArgumentNotValidException}、{@code
 * BindException}、{@code ConstraintViolationException}）， 以及访问不存在的静态资源触发的 {@code
 * NoResourceFoundException}。
 *
 * @author Yun Jiao
 */
@Controller
@Validated
public class ValidationController {

  // 1. 触发 MethodArgumentNotValidException（@RequestBody JSON 校验）
  /**
   * JSON 请求体校验示例
   *
   * @param request JSON 请求体
   * @return 校验通过提示
   */
  @PostMapping("/api/test/json")
  @ResponseBody
  public Result<String> testJson(@Valid @RequestBody JsonRequest request) {
    return Result.success("JSON 校验通过");
  }

  // 2. 触发 BindException（表单/@ModelAttribute 校验）
  /**
   * 表单校验示例
   *
   * @param request 表单请求
   * @return 校验通过提示
   */
  @PostMapping("/api/test/form")
  @ResponseBody
  public Result<String> testForm(@Valid @ModelAttribute FormRequest request) {
    return Result.success("表单校验通过");
  }

  // 3. 触发 ConstraintViolationException（@RequestParam 校验）
  /**
   * 请求参数校验示例
   *
   * @param name 请求参数，需满足非空且长度 2~10
   * @return 校验通过提示
   */
  @GetMapping("/api/test/param")
  @ResponseBody
  public Result<String> testParam(@RequestParam @NotBlank @Size(min = 2, max = 10) String name) {
    return Result.success("参数校验通过：" + name);
  }

  // 4. 触发 NoResourceFoundException（访问不存在的静态资源）
  // 不需要额外代码，只要前端请求一个不存在的资源路径即可
}
