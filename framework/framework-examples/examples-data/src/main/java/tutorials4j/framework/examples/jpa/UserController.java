package tutorials4j.framework.examples.jpa;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * 用户接口
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    // 创建用户
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // 更新用户
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequest request) {
        User user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }

    // 查询单个用户
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // 分页查询所有用户（支持排序）
    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    // 按姓名模糊分页查询
    @GetMapping("/search")
    public ResponseEntity<Page<User>> searchUsersByName(
            @RequestParam(name="name") String name,
            Pageable pageable) {
        Page<User> users = userService.searchUsersByName(name, pageable);
        return ResponseEntity.ok(users);
    }

}
