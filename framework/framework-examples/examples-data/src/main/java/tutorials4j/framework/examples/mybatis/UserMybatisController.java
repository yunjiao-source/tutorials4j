package tutorials4j.framework.examples.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/mybatis/users")
public class UserMybatisController {
    private final UserMybatisService userMybatisService;

    // 创建用户
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody UserCreateDTO createDTO) {
        User user = new User();
        BeanUtils.copyProperties(createDTO, user);
        userMybatisService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // 更新用户
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable("id") Long id,
                                       @Valid @RequestBody UserUpdateDTO updateDTO) {
        User existing = userMybatisService.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        BeanUtils.copyProperties(updateDTO, existing, "id");
        userMybatisService.updateById(existing);
        return ResponseEntity.ok(existing);
    }

    // 查询单个用户
    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable("id") Long id) {
        User user = userMybatisService.getById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    // 删除用户（逻辑删除）
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        boolean removed = userMybatisService.removeById(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // 分页查询所有用户（支持模糊搜索）
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
        wrapper.orderByDesc(User::getId);  // 按id倒序
        Page<User> result = userMybatisService.page(page, wrapper);
        return ResponseEntity.ok(result);
    }
}
