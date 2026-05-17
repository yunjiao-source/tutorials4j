package tutorials4j.framework.examples.xss;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import tutorials4j.framework.web.mvc.support.XssJacksonSimpleModule;

/**
 * XSS 防护示例接口。
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequestMapping("/xss")
public class XssDemoController {

  /**
   * GET 请求示例：通过查询参数传递可能含有 XSS 的内容。
   *
   * <p>由于 {@link tutorials4j.framework.web.mvc.filter.XssRequestFilter} 会包装 HttpServletRequest， 因此
   * {@code @RequestParam} 获取到的值已经被 AntiSamy 清洗过。
   *
   * @param content 用户输入的文本（自动清洗）
   * @return 清洗后的内容
   */
  @GetMapping("/param")
  public String handleParam(@RequestParam("content") String content) {
    log.info("Received param (after XSS filter): {}", content);
    return "Cleaned content: " + content;
  }

  /**
   * POST 请求示例：接收 JSON 对象，其中的字符串字段会被 {@link XssJacksonSimpleModule} 注册的 {@link
   * tutorials4j.framework.web.mvc.support.XssJsonDeserializer} 自动清洗。
   *
   * @param request JSON 请求体
   * @return 清洗后的对象
   */
  @PostMapping("/json")
  public CommentDto handleJson(@RequestBody CommentDto request) {
    log.info("Received JSON (after XSS cleaning): {}", request);
    return request;
  }
}

/** 示例 DTO，包含可能携带 XSS 的字符串字段。 */
@Data
class CommentDto {
  private String username;
  private String comment;
}
