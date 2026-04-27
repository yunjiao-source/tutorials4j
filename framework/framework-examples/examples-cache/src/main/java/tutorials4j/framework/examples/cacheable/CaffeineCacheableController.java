package tutorials4j.framework.examples.cacheable;

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
@RequestMapping("/caffeine/cacheable")
@RequiredArgsConstructor
public class CaffeineCacheableController {
    private final CaffeineCacheableService caffeineCacheableService;

    @GetMapping("users")
    public String getUser(@RequestParam("id") Long id) {
        return caffeineCacheableService.getUser(id);
    }

    @GetMapping("orders")
    public String getOrder(@RequestParam("id") Long id) {
        return caffeineCacheableService.getOrder(id);
    }

    @GetMapping("cars")
    public String getCar(@RequestParam("id") Long id) {
        return caffeineCacheableService.getCar(id);
    }

}
