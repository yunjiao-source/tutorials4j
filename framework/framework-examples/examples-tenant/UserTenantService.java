package tutorials4j.framework.examples;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tutorials4j.framework.common.core.bean.TenantContextHolder;

import java.util.List;

/**
 * 服務
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserTenantService {
    private final UserTenantRepository userTenantRepository;

    public List<UserTenant> getAllUsers() {
        return userTenantRepository.findAll();
    }

    @Async
    public void findAsynById(Long id) {
        log.info("多线程租户: {}", TenantContextHolder.get());
    }
}
