package tutorials4j.framework.examples;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * 用户接口
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/user-tenant")
@RequiredArgsConstructor
public class UserTenantController {
    private final UserTenantRepository userTenantRepository;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody UserTenant user) {
        userTenantRepository.save(user);
        return ResponseEntity.ok("创建用户成功");
    }

    @GetMapping("/get")
    public List<UserTenant> tenant() {
        return userTenantRepository.findAll();
    }
}
