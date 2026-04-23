package tutorials4j.framework.examples.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.core.bean.TenantContextHolder;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("cache")
@RequiredArgsConstructor
public class CacheController {
    private final CacheService cacheService;

    @GetMapping("users")
    public String getUser(@RequestParam("id") Long id) {
        return cacheService.getUser(id);
    }

    @GetMapping("orders")
    public String getOrder(@RequestParam("id") Long id) {
        return cacheService.getOrder(id);
    }

    @GetMapping("cars")
    public String getCar(@RequestParam("id") Long id) {
        return cacheService.getCar(id);
    }

    @GetMapping("tenant-users")
    public String getTenantUser(@RequestParam("id") Long id) {
        TenantContextHolder.set("DEMO");
        return cacheService.getUser(id);
    }


}
