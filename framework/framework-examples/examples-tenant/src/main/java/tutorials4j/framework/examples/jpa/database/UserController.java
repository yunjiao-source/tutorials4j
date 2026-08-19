package tutorials4j.framework.examples.jpa.database;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户 REST 接口，提供用户的创建、查询及异步查询等示例操作。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
  private final UserRepository userRepository;
  private final UserService userService;

  /**
   * 创建用户。
   *
   * @param user 用户信息
   * @return 创建成功后的用户信息
   */
  @PostMapping("/create")
  public ResponseEntity<?> create(@RequestBody User user) {
    User u = userRepository.save(user);
    return ResponseEntity.ok(u);
  }

  /**
   * 查询全部用户。
   *
   * @return 用户列表
   */
  @GetMapping
  public ResponseEntity<?> getAll() {
    return ResponseEntity.ok(userRepository.findAll());
  }

  /**
   * 根据 ID 查询单个用户。
   *
   * @param id 用户 ID
   * @return 用户信息，不存在时为 null
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable("id") Long id) {
    User user = userRepository.findById(id).orElse(null);
    return ResponseEntity.ok(user);
  }

  /**
   * 异步查询用户（示例：演示异步方法在多线程下的租户上下文传递）。
   *
   * @param id 用户 ID
   * @return 空响应体
   */
  @GetMapping("/asyn/{id}")
  public ResponseEntity<?> getAsyn(@PathVariable("id") Long id) {
    userService.findAsynById(id);
    return ResponseEntity.ok().build();
  }
}
