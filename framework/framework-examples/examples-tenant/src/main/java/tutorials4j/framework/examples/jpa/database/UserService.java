package tutorials4j.framework.examples.jpa.database;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tutorials4j.framework.common.core.TenantContextHolder;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
        log.info("多线程租户: {}", TenantContextHolder.get());
        TimeUnit.SECONDS.sleep(3);
    }
}
