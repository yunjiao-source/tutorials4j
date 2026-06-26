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

@Controller
@Validated
public class ValidationController {

  // 1. 触发 MethodArgumentNotValidException（@RequestBody JSON 校验）
  @PostMapping("/api/test/json")
  @ResponseBody
  public Result<String> testJson(@Valid @RequestBody JsonRequest request) {
    return Result.success("JSON 校验通过");
  }

  // 2. 触发 BindException（表单/@ModelAttribute 校验）
  @PostMapping("/api/test/form")
  @ResponseBody
  public Result<String> testForm(@Valid @ModelAttribute FormRequest request) {
    return Result.success("表单校验通过");
  }

  // 3. 触发 ConstraintViolationException（@RequestParam 校验）
  @GetMapping("/api/test/param")
  @ResponseBody
  public Result<String> testParam(@RequestParam @NotBlank @Size(min = 2, max = 10) String name) {
    return Result.success("参数校验通过：" + name);
  }

  // 4. 触发 NoResourceFoundException（访问不存在的静态资源）
  // 不需要额外代码，只要前端请求一个不存在的资源路径即可
}
