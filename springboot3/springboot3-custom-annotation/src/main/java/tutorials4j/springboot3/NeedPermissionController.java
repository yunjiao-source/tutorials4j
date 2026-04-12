package tutorials4j.springboot3;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * NeedPermission示例
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("need-permission")
public class NeedPermissionController {

    // 需要 user:query 权限
    @NeedPermission("user:query")
    @GetMapping("/user/query")
    public String queryUser() {
        return "查询用户列表成功";
    }

    // 需要 user:add 权限（当前用户无此权限）
    @NeedPermission("user:add")
    @GetMapping("/user/add")
    public String addUser() {
        return "新增用户成功";
    }

    // 需要多个权限（同时拥有 order:query 和 order:add）
    @NeedPermission({"order:query", "order:add"})
    @GetMapping("/order/add")
    public String addOrder() {
        return "新增订单成功";
    }
}

