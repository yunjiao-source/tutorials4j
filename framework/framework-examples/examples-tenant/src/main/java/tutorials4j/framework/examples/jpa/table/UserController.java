package tutorials4j.framework.examples.jpa.table;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户接口控制器。
 *
 * <p>提供用户的新增、查询等 REST 接口，演示多租户场景下的数据读写。
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
   * @return 创建后的用户
   */
  @PostMapping("/create")
  public ResponseEntity<?> create(@RequestBody User user) {
    User newUser = userRepository.save(user);
    return ResponseEntity.ok(newUser);
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
   * 根据 ID 查询用户。
   *
   * @param id 用户 ID
   * @return 匹配的用户，若不存在返回 {@code null}
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable("id") Long id) {
    User user = userRepository.findById(id).orElse(null);
    return ResponseEntity.ok(user);
  }

  /**
   * 异步查询指定用户，演示多线程下的租户上下文传递。
   *
   * @param id 用户 ID
   * @return 空响应
   */
  @GetMapping("/asyn/{id}")
  public ResponseEntity<?> getAsyn(@PathVariable("id") Long id) {
    userService.findAsynById(id);
    return ResponseEntity.ok().build();
  }
}
