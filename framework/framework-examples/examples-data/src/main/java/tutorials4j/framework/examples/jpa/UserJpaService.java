package tutorials4j.framework.examples.jpa;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 服務
 *
 * @author Yun Jiao
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserJpaService {
    private final UserJpaRepository userJpaRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 创建用户（自动生成secretKey并加密密码）

    public User createUser(CreateUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        user.setSecretKey(UUID.randomUUID().toString());  // 生成随机密钥
        user.setSex(request.getSex());
        return userJpaRepository.save(user);
    }

    // 更新用户（仅更新传入的非空字段）
    public User updateUser(Long id, UpdateUserRequest request) {
        User user = userJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在，id: " + id));
        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user.setEmail(request.getEmail());
        }
        if (request.getAge() != null) {
            user.setAge(request.getAge());
        }
        return userJpaRepository.save(user);
    }

    // 根据ID查询用户
    public User getUserById(Long id) {
        return userJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在，id: " + id));
    }

    // 删除用户
    public void deleteUser(Long id) {
        if (!userJpaRepository.existsById(id)) {
            throw new EntityNotFoundException("用户不存在，id: " + id);
        }
        userJpaRepository.deleteById(id);
    }

    // 分页查询所有用户
    public Page<User> getAllUsers(Pageable pageable) {
        return userJpaRepository.findAll(pageable);
    }

    // 按姓名模糊分页查询
    public Page<User> searchUsersByName(String name, Pageable pageable) {
        return userJpaRepository.findByNameContaining(name, pageable);
    }
}