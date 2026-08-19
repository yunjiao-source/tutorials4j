package tutorials4j.framework.examples.exception;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 异常处理示例控制器。
 *
 * <p>提供多个演示接口，用于展示框架对运行时异常、参数校验异常以及自定义业务异常的统一处理能力。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("exception")
public class ExceptionController {
  /**
   * 演示一：触发 {@link NumberFormatException} 运行时异常。
   *
   * @return 解析后的整数值
   */
  @GetMapping("demo1")
  public Integer demo1() {
    String num = "a";
    return Integer.valueOf(num);
  }

  /** 演示二：使用 {@code Assert.hasText} 触发参数校验异常（{@link IllegalArgumentException}）。 */
  @GetMapping("demo2")
  public void demo2() {
    String num = " ";
    Assert.hasText(num, "num must not be null or empty");
  }

  /** 演示三：抛出自定义业务异常 {@link CustomErrorCode#CUSTOM_EXCEPTION}，并附带异常参数。 */
  @GetMapping("demo3")
  public void demo3() {
    throw CustomErrorCode.CUSTOM_EXCEPTION.throwed("demo3方法出现异常").param("key", "value");
  }
}
