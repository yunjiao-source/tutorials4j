package tutorials4j.framework.examples.jpa.table;

import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tutorials4j.framework.common.core.TenantContextHolder;

/**
 * 服務
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Async
  @SneakyThrows
  public void findAsynById(Long id) {
    List<User> users = getAllUsers();
    log.info("多线程租户: {}, 用户数：{}", TenantContextHolder.get(), users.size());
    TimeUnit.SECONDS.sleep(3);
  }
}
