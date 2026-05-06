package tutorials4j.springboot3.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 接口
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.list();
    }

    @PostMapping
    public User createUser(@RequestBody CreateUserModel model) {
        User user = new User();
        user.setName(model.getName());
        userService.save(user);
        return user;
    }
}