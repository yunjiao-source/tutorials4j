package tutorials4j.springboot3.common.jpa;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 服務
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public String login(UserDto userInfo) {
    User user = userRepository.findByName(userInfo.username());
    if (user != null) {
      if (user.getPassword().equals(userInfo.password())) {
        return UUID.randomUUID().toString();
      }
    }
    throw new RuntimeException("用户名或密码错误");
  }
}
