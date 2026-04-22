package tutorials4j.framework.examples;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服務
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class UserTenantService {
    private final UserTenantRepository userTenantRepository;

    public List<UserTenant> getAllUsers() {
        return userTenantRepository.findAll();
    }
}
