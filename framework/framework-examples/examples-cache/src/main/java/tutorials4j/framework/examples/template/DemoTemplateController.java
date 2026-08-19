package tutorials4j.framework.examples.template;

import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.cache.core.exception.CounterOverflowException;

/**
 * 缓存模板使用示例控制器，演示验证码缓存与计数器缓存的接口调用方式。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/template")
@RequiredArgsConstructor
public class DemoTemplateController {
  private final CaptchaCacheTemplate captchaCacheTemplate;
  private final SimpleCounterTemplate simpleCounterTemplate;

  /**
   * 生成一个验证码并写入缓存，返回验证码键与验证码值。
   *
   * @return 键值对，左侧为验证码缓存键，右侧为验证码内容
   */
  @GetMapping("get")
  public Pair<String, String> get() {
    String key = IdUtil.fastSimpleUUID();
    String captcha = captchaCacheTemplate.create(key);
    return Pair.of(key, captcha);
  }

  /**
   * 校验用户输入的验证码是否与缓存中的值一致。
   *
   * @param key 验证码缓存键
   * @param input 用户输入的验证码
   * @return 校验通过返回 true，否则返回 false
   */
  @GetMapping("check")
  public Boolean check(@RequestParam("key") String key, @RequestParam("input") String input) {
    return captchaCacheTemplate.check(key, input);
  }

  /**
   * 对指定键进行计数，演示计数器缓存的使用。
   *
   * @param key 计数器缓存键
   * @return 当前计数结果
   * @throws CounterOverflowException 计数超过上限时抛出
   */
  @GetMapping("counter")
  public Integer check(@RequestParam("key") String key) throws CounterOverflowException {
    return simpleCounterTemplate.counting(key, 5);
  }
}
