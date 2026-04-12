package tutorials4j.springboot3;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping
public class DemoController {
    // 1. 成功响应（无数据，直接返回null，自动封装为Result.success()）
    @GetMapping("/success/empty")
    public void successEmpty() {
        // 无返回值（void），自动封装为 Result{code:200, message:"操作成功", data:null}
    }
    // 2. 成功响应（带单个实体数据，直接返回User，自动封装为Result<User>）
    @GetMapping("/success/single")
    public User successSingle() {
        // 模拟查询用户数据，直接返回User，无需手动封装
        return new User(1L, "张三", 20, "zhangsan@163.com");
    }
    // 3. 成功响应（带列表数据，直接返回List<User>，自动封装为Result<List<User>>）
    @GetMapping("/success/list")
    public List<User> successList() {
        // 模拟查询用户列表，直接返回List，无需手动封装
        List<User> userList = new ArrayList<>();
        userList.add(new User(1L, "张三", 20, "zhangsan@163.com"));
        userList.add(new User(2L, "李四", 22, "lisi@163.com"));
        return userList;
    }
    // 4. 分页响应（手动返回PageResult，不拦截，保持分页格式）
    @GetMapping("/success/page")
    public PageResult<User> successPage(
            @RequestParam(name="currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(name="pageSize", defaultValue = "2") Integer pageSize
    ) {
        // 分页场景建议手动返回PageResult，确保分页字段正常返回
        List<User> userList = new ArrayList<>();
        userList.add(new User(1L, "张三", 20, "zhangsan@163.com"));
        userList.add(new User(2L, "李四", 22, "lisi@163.com"));
        Long total = 5L; // 模拟总条数
        return PageResult.pageSuccess(userList, total, currentPage, pageSize);
    }
    // 5. 失败响应（手动返回Result.fail，不拦截，自定义失败提示）
    @GetMapping("/fail/param")
    public Result<?> failParam() {
        // 失败/异常场景，可手动返回Result.fail，也可抛异常（全局异常处理器捕获）
        return Result.fail(ResultCode.PARAM_NULL, "用户ID不能为空");
    }
    // 6. 业务异常响应（抛出自定义业务异常，全局异常处理器捕获，自动封装）
    @GetMapping("/fail/business")
    public User failBusiness() {
        // 模拟业务逻辑异常（如用户不存在），抛出异常，无需手动返回Result
        throw new BusinessException(ResultCode.DATA_NULL, "查询的用户不存在");
    }
    // 7. 系统异常响应（模拟未捕获的异常，全局异常处理器捕获，自动封装）
    @GetMapping("/error/system")
    public User errorSystem() {
        // 模拟空指针异常，抛出异常，无需手动处理返回格式
        String str = null;
        str.length();
        return null;
    }
    // 模拟实体类（实际开发中单独放在entity包）
    public record User(Long id, String name,Integer age, String email) {
    }
}

