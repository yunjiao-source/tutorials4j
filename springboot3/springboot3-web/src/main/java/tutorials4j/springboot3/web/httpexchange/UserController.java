package tutorials4j.springboot3.web.httpexchange;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例
 *
 * @author yangyunjiao
 */
@RestController
@RequestMapping("/users")
public class UserController {

  private static final List<User> DATAS = new ArrayList<>();

  static {
    DATAS.add(new User(1L, "张三", 22));
    DATAS.add(new User(2L, "李四", 32));
    DATAS.add(new User(3L, "王五", 33));
    DATAS.add(new User(4L, "赵六", 26));
    DATAS.add(new User(5L, "田七", 29));
    DATAS.add(new User(6L, "嘿哈", 44));
  }

  @PostMapping
  public ResponseEntity<Void> save(@RequestBody User user) {
    DATAS.add(user);
    return ResponseEntity.created(URI.create(String.format("/users/%s", user.id()))).build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
    DATAS.removeIf(user -> user.id() == id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("")
  public ResponseEntity<Void> update(@RequestBody User user) {
    DATAS.stream()
        .filter(u -> u.id() == user.id())
        .findFirst()
        .map(u -> new User(u.id(), user.name(), user.age()));
    return ResponseEntity.noContent().build();
  }

  @GetMapping("")
  public ResponseEntity<List<User>> list() {
    return ResponseEntity.ok(DATAS);
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> get(@PathVariable("id") Long id) {
    return ResponseEntity.ok(DATAS.stream().filter(u -> u.id() == id).findFirst().orElse(null));
  }

  // 测试异常情况
  @GetMapping("/exception")
  public ResponseEntity<Void> exce() {
    System.out.println(1 / 0);
    return ResponseEntity.noContent().build();
  }
}
