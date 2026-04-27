package tutorials4j.framework.examples.tenant;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.examples.jpa.User;
import tutorials4j.framework.examples.jpa.UserRepository;

/**
 * 勿租户过滤器示例
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/non-tenant")
@RequiredArgsConstructor
public class NonTenantController {
    private final UserRepository userRepository;

    @GetMapping("/user/{id}")
    public User demoUser(@PathVariable("id") Long id) {
        return userRepository.findById(id).orElse(null);
    }


}
