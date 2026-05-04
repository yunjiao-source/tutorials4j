package tutorials4j.framework.examples.template;

import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.core.exception.CounterOverflowException;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/template")
@RequiredArgsConstructor
public class DemoTemplateController {
    private final CaptchaCacheTemplate captchaCacheTemplate;
    private final SimpleCounterTemplate simpleCounterTemplate;

    @GetMapping("get")
    public Pair<String, String> get() {
        String key = IdUtil.fastSimpleUUID();
        String captcha = captchaCacheTemplate.create(key);
        return Pair.of(key, captcha);
    }

    @GetMapping("check")
    public Boolean check(@RequestParam("key") String key,@RequestParam("input") String input) {
        return captchaCacheTemplate.check(key, input);
    }

    @GetMapping("counter")
    public Integer check(@RequestParam("key") String key) throws CounterOverflowException {
        return simpleCounterTemplate.counting(key, 5);
    }



}
