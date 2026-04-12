package tutorials4j.springboot3;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody User user) {
        userRepository.save(user);
        return ResponseEntity.ok("创建用户成功");
    }
}
