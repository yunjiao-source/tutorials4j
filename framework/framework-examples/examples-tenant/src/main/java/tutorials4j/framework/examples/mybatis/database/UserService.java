package tutorials4j.framework.examples.mybatis.database;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 用户服务实现类。
 *
 * <p>继承 MyBatis-Plus 的 {@link ServiceImpl}，在保存与更新用户时对密码进行 MD5 加密并生成密钥。
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

  // 重写保存方法，加密密码并生成secretKey
  /**
   * 保存用户，保存前对密码进行 MD5 加密并生成随机 secretKey。
   *
   * @param entity 用户实体
   * @return 是否保存成功
   */
  public boolean save(User entity) {
    // 密码加密（简单示例用MD5，实际推荐BCrypt）
    String encryptedPwd =
        DigestUtils.md5DigestAsHex(entity.getPassword().getBytes(StandardCharsets.UTF_8));
    entity.setPassword(encryptedPwd);
    entity.setSecretKey(UUID.randomUUID().toString());
    return super.save(entity);
  }

  // 重写更新方法，如果密码有变更则加密
  /**
   * 更新用户，若密码有变更则对密码进行 MD5 加密。
   *
   * @param entity 用户实体
   * @return 是否更新成功
   */
  public boolean updateById(User entity) {
    if (entity.getPassword() != null && !entity.getPassword().isEmpty()) {
      String encryptedPwd =
          DigestUtils.md5DigestAsHex(entity.getPassword().getBytes(StandardCharsets.UTF_8));
      entity.setPassword(encryptedPwd);
    }
    return super.updateById(entity);
  }

  /**
   * 异步查询指定用户并模拟耗时处理，演示 {@code @Async} 异步调用。
   *
   * @param id 用户 ID
   */
  @Async
  public void async(Long id) {
    User user = getById(id);
    log.info(">>> {}", user);

    try {
      TimeUnit.SECONDS.sleep(3);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
