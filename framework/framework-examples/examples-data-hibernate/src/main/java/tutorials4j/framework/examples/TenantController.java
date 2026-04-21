package tutorials4j.framework.examples;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/tenant")
@RequiredArgsConstructor
public class TenantController {
    private final UserRepository userRepository;

    @GetMapping("/user/{id}")
    public User demoUser(@PathVariable("id") Long id) {
        return userRepository.findById(id).orElse(null);
    }


}
