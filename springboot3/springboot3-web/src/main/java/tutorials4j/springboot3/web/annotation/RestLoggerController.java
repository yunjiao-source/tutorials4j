package tutorials4j.springboot3.web.annotation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接口示例
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("logger")
public class RestLoggerController {

  // 指定接口描述，默认打印参数和结果
  @RestLogger("用户查询接口")
  @GetMapping("/user")
  public String getUser(@RequestParam("userId") String userId) {
    // 模拟业务逻辑
    return "用户信息：userId=" + userId + ", name=张三";
  }

  // 不打印响应结果
  @RestLogger(value = "订单创建接口", printResult = false)
  @GetMapping("/order")
  public String createOrder(@RequestParam("orderNo") String orderNo) {
    return "订单创建成功：" + orderNo;
  }
}
