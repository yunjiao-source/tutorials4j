package tutorials4j.framework.examples;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
public class DemoController {
    private final UserRepository userRepository;


    @GetMapping("users")
    public List<User> users() {
        return userRepository.findAll();
    }
}
