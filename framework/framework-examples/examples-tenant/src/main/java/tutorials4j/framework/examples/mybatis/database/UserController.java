package tutorials4j.framework.examples.mybatis.database;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理示例控制器。
 *
 * <p>提供用户的新增、更新、查询、异步处理、删除与分页查询接口，演示 MyBatis-Plus 多数据源下的 CRUD 操作。
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  // 创建用户
  /**
   * 创建用户。
   *
   * @param createDTO 创建用户请求参数
   * @return 创建成功后的用户信息
   */
  @PostMapping
  public ResponseEntity<User> create(@Valid @RequestBody UserCreateDTO createDTO) {
    User user = new User();
    BeanUtils.copyProperties(createDTO, user);
    userService.save(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  // 更新用户
  /**
   * 更新用户信息。
   *
   * @param id 用户 ID
   * @param updateDTO 更新用户请求参数
   * @return 更新后的用户信息；用户不存在时返回 404
   */
  @PutMapping("/{id}")
  public ResponseEntity<User> update(
      @PathVariable("id") Long id, @Valid @RequestBody UserUpdateDTO updateDTO) {
    User existing = userService.getById(id);
    if (existing == null) {
      return ResponseEntity.notFound().build();
    }
    BeanUtils.copyProperties(updateDTO, existing, "id");
    userService.updateById(existing);
    return ResponseEntity.ok(existing);
  }

  // 查询单个用户
  /**
   * 查询单个用户。
   *
   * @param id 用户 ID
   * @return 用户信息；用户不存在时返回 404
   */
  @GetMapping("/{id}")
  public ResponseEntity<User> get(@PathVariable("id") Long id) {
    User user = userService.getById(id);
    return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
  }

  /**
   * 异步处理指定用户，立即返回，实际业务在后台线程执行。
   *
   * @param id 用户 ID
   * @return 空响应体
   */
  @GetMapping("/async/{id}")
  public ResponseEntity<Void> async(@PathVariable("id") Long id) {
    userService.async(id);
    return ResponseEntity.ok().build();
  }

  // 删除用户（逻辑删除）
  /**
   * 删除用户（逻辑删除）。
   *
   * @param id 用户 ID
   * @return 删除成功时返回 204；用户不存在时返回 404
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
    boolean removed = userService.removeById(id);
    return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }

  // 分页查询所有用户（支持模糊搜索）
  /**
   * 分页查询所有用户（支持按姓名模糊搜索）。
   *
   * @param current 当前页码，默认 1
   * @param size 每页条数，默认 10
   * @param name 姓名关键字，可为空
   * @return 用户分页结果
   */
  @GetMapping
  public ResponseEntity<Page<User>> page(
      @RequestParam(name = "current", defaultValue = "1") int current,
      @RequestParam(name = "size", defaultValue = "10") int size,
      @RequestParam(name = "name", required = false) String name) {
    Page<User> page = new Page<>(current, size);
    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
    if (name != null && !name.isEmpty()) {
      wrapper.like(User::getName, name);
    }
    wrapper.orderByDesc(User::getId); // 按id倒序
    Page<User> result = userService.page(page, wrapper);
    return ResponseEntity.ok(result);
  }
}
