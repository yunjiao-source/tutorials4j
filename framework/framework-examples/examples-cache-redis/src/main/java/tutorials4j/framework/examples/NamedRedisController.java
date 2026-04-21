package tutorials4j.framework.examples;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("named-redis-cache")
@RequiredArgsConstructor
public class NamedRedisController {
    private final NamedRedisCacheService namedRedisCacheService;

    @GetMapping("users")
    public String getUser(@RequestParam("id") Long id) {
        return namedRedisCacheService.getUser(id);
    }

    @GetMapping("orders")
    public String getOrder(@RequestParam("id") Long id) {
        return namedRedisCacheService.getOrder(id);
    }

    @GetMapping("cars")
    public String getCar(@RequestParam("id") Long id) {
        return namedRedisCacheService.getCar(id);
    }


}
