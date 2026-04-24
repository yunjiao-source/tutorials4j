package tutorials4j.framework.examples;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody UserTenant user) {
        UserTenant newUser = userTenantRepository.save(user);
        return ResponseEntity.ok(newUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        UserTenant user = userTenantRepository.findById(id).orElse(null);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/asyn/{id}")
    public ResponseEntity<?> getAsyn(@PathVariable("id") Long id) {
        userService.findAsynById(id);
        return ResponseEntity.ok().build();
    }
}
