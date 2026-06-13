// XssDemoController.java
package tutorials4j.framework.examples.xss;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/xss-demo")
public class XssDemoController {

  /** 演示表单参数清洗（由 XssHttpServletRequestWrapper 自动完成） 返回清洗后的结果 */
  @PostMapping("/form")
  @ResponseBody
  public Map<String, String> testFormParam(@RequestParam("inputText") String inputText) {
    // 注意：这里的 inputText 已经被 XssHttpServletRequestWrapper 清洗过
    log.debug("表单参数清洗后: {}", inputText);
    Map<String, String> result = new HashMap<>();
    result.put("cleaned", inputText);
    return result;
  }

  /** 演示 JSON 请求体清洗（由 XssJsonDeserializer 自动完成） */
  @PostMapping("/json")
  @ResponseBody
  public Map<String, String> testJsonBody(@RequestBody XssRequestDto dto) {
    // dto.getContent() 已在反序列化时被 XssJsonDeserializer 清洗
    log.debug("JSON 请求体清洗后: {}", dto.getContent());
    Map<String, String> result = new HashMap<>();
    result.put("cleaned", dto.getContent());
    return result;
  }
}
