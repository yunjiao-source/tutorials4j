package tutorials4j.framework.examples.multi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.examples.Car;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/multi-level/cacheable")
@RequiredArgsConstructor
public class MultiCacheableController {
    private final MultiCacheableService multiCacheableService;

    @GetMapping("users")
    public String getUser(@RequestParam("id") Long id) {
        return multiCacheableService.getUser(id);
    }

    @GetMapping("orders")
    public String getOrder(@RequestParam("id") Long id) {
        return multiCacheableService.getOrder(id);
    }

    @GetMapping("cars")
    public Car getCar(@RequestParam("id") Long id) {
        return multiCacheableService.getCar(id);
    }

}
