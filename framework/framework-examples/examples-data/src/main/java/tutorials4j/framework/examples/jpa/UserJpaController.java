package tutorials4j.framework.examples.jpa;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tutorials4j.framework.data.core.jpa.PageResult;

/**
 * 用户接口
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/jpa/users")
public class UserJpaController {
  private final UserJpaService userJpaService;

  // 创建用户
  @PostMapping
  public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
    User user = userJpaService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  // 更新用户
  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(
      @PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequest request) {
    User user = userJpaService.updateUser(id, request);
    return ResponseEntity.ok(user);
  }

  // 查询单个用户
  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
    User user = userJpaService.getUserById(id);
    return ResponseEntity.ok(user);
  }

  // 删除用户
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
    userJpaService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  // 分页查询所有用户（支持排序）
  @GetMapping
  public ResponseEntity<PageResult<User>> getAllUsers(Pageable pageable) {
    Page<User> users = userJpaService.getAllUsers(pageable);
    return ResponseEntity.ok(PageResult.of(users));
  }

  // 按姓名模糊分页查询
  @GetMapping("/search")
  public ResponseEntity<PageResult<User>> searchUsersByName(
      @RequestParam(name = "name") String name, Pageable pageable) {
    Page<User> users = userJpaService.searchUsersByName(name, pageable);
    return ResponseEntity.ok(PageResult.of(users));
  }
}
