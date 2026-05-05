package tutorials4j.framework.examples.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.web.core.annotation.AccessLimited;
import tutorials4j.framework.web.core.annotation.Idempotent;

import java.io.IOException;

/**
 * trace 示例接口
 *
 * @author yangyunjiao
 */
@Slf4j
@RestController
@RequestMapping("security")
@RequiredArgsConstructor
public class SecurityController {

    @Idempotent
    @GetMapping("idempotent")
    public String idempotent() throws IOException {
        return "idempotent";
    }

    @AccessLimited(maxTimes = 4)
    @GetMapping("access-limited")
    public String accessLimited() {
        return "accessLimited";
    }


}
