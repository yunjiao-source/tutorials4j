package tutorials4j.springboot3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tutorials4j.springboot3.google.GoogleAuthUtil;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final GoogleAuthUtil googleAuthUtil;

  /**
   * 为用户生成并保存新的 secretKey
   *
   * @param username 用户名
   * @return 生成的 secretKey
   */
  public String createSecretKeyForUser(String username) {
    String secretKey = googleAuthUtil.generateSecretKey();
    User user = userRepository.findByName(username);
    user.setSecretKey(secretKey);
    userRepository.save(user);
    return secretKey;
  }

  /**
   * 验证用户的 Google Authenticator 验证码
   *
   * @param username 用户名
   * @param code 验证码
   * @return 验证结果
   */
  public boolean verifyCode(String username, int code) {
    User user = userRepository.findByName(username);
    return googleAuthUtil.verifyCode(user.getSecretKey(), code);
  }

  // 校验密码
  public boolean verifyPassword(String username, String password) {
    User user = userRepository.findByName(username);
    return (user != null && password != null && password.equals(user.getPassword()));
  }
}
