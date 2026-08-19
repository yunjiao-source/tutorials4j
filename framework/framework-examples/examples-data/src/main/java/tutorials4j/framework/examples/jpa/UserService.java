package tutorials4j.framework.examples.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;
import tutorials4j.framework.data.hibernate.domain.BaseService;

/**
 * 用户业务服务。
 *
 * <p>继承 {@link BaseService}，提供用户实体的基础业务能力。
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class UserService implements BaseService<User, Long> {

  private final UserRepository userRepository;

  /**
   * 返回用户数据仓库实例。
   *
   * @return 用户数据仓库
   */
  @Override
  public BaseRepository<User, Long> getRepository() {
    return userRepository;
  }
}
