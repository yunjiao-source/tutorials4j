package tutorials4j.framework.examples.mybatis;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User>{

    // 重写保存方法，加密密码并生成secretKey
    public boolean save(User entity) {
        // 密码加密（简单示例用MD5，实际推荐BCrypt）
        String encryptedPwd = DigestUtils.md5DigestAsHex(entity.getPassword().getBytes(StandardCharsets.UTF_8));
        entity.setPassword(encryptedPwd);
        entity.setSecretKey(UUID.randomUUID().toString());
        return super.save(entity);
    }

    // 重写更新方法，如果密码有变更则加密
    public boolean updateById(User entity) {
        if (entity.getPassword() != null && !entity.getPassword().isEmpty()) {
            String encryptedPwd = DigestUtils.md5DigestAsHex(entity.getPassword().getBytes(StandardCharsets.UTF_8));
            entity.setPassword(encryptedPwd);
        }
        return super.updateById(entity);
    }
}
