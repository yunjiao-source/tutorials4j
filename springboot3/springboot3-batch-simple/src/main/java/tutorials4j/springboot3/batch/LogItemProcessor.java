package tutorials4j.springboot3.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import tutorials4j.springboot3.User;

/**
 * 用户日志
 *
 * @author Yun Jiao
 */
@Slf4j
public class LogItemProcessor implements ItemProcessor<User, User> {
    @Override
    public User process(User item) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Processing: {}", item.getName());
        }
        return item;
    }
}
