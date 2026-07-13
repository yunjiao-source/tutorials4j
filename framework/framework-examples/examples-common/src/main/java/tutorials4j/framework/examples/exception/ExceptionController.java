package tutorials4j.framework.examples.exception;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("exception")
public class ExceptionController {
  @GetMapping("demo1")
  public Integer demo1() {
    String num = "a";
    return Integer.valueOf(num);
  }

  @GetMapping("demo2")
  public void demo2() {
    String num = " ";
    Assert.hasText(num, "num must not be null or empty");
  }

  @GetMapping("demo3")
  public void demo3() {
    throw CustomErrorCode.CUSTOM_EXCEPTION.throwed("demo3方法出现异常").param("key", "value");
  }
}
