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
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody User user) {
        userRepository.save(user);
        return ResponseEntity.ok("创建用户成功");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        User user = userRepository.findById(id).orElse(null);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/asyn/{id}")
    public ResponseEntity<?> getAsyn(@PathVariable("id") Long id) {
        userService.findAsynById(id);
        return ResponseEntity.ok().build();
    }
}
