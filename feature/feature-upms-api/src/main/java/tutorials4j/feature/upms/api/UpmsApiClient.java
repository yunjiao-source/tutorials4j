package tutorials4j.feature.upms.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import tutorials4j.feature.upms.model.UserVO;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@HttpExchange
public interface UpmsApiClient {
  @GetExchange("upms/users/{id}")
  UserVO getById(@PathVariable("id") String id);

  @GetExchange("upms/users/by-username/{username}")
  UserVO getByUsername(@PathVariable("username") String username);
}
