package tutorials4j.framework.data.core.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface DataUtils {
    static Optional<String> getAccount() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(authentication.getName());
    }
}
