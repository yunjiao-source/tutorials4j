package tutorials4j.springboot3.batch.simple.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import tutorials4j.springboot3.common.jpa.User;

/**
 * 转换处理器
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class TransferItemProcessor implements ItemProcessor<UserCsvRecord, User> {

  @Override
  public User process(UserCsvRecord item) throws Exception {
    return User.of(item.getName(), item.getEmail());
  }
}
