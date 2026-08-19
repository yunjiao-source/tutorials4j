package tutorials4j.framework.examples.jpa.database;

import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tutorials4j.framework.common.core.TenantContextHolder;

/**
 * 用户服务，提供用户查询及多线程租户上下文传递的异步示例。
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  /**
   * 查询全部用户。
   *
   * @return 用户列表
   */
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  /**
   * 异步查询用户并记录租户上下文信息（示例方法，内含 3 秒延时模拟耗时操作）。
   *
   * @param id 用户 ID
   */
  @Async
  @SneakyThrows
  public void findAsynById(Long id) {
    List<User> users = getAllUsers();
    log.info("多线程租户: {}, 用户数：{}", TenantContextHolder.get(), users.size());
    TimeUnit.SECONDS.sleep(3);
  }
}
