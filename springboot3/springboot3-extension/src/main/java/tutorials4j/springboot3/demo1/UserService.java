package tutorials4j.springboot3.demo1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService implements CloseableClient {
  @AutoRedis private RedisUtil redisUtil;

  public UserVO createUser() {
    UserVO vo = new UserVO();
    vo.setName("张三");
    vo.setPhone("13812345678");
    vo.setIdCard("110101199001011234");
    return vo;
  }

  @Override
  public void close() {
    log.info(">>>销毁");
  }
}
