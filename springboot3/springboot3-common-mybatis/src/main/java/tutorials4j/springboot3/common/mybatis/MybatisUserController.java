package tutorials4j.springboot3.common.mybatis;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接口
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/users")
public class MybatisUserController {

  @Autowired private MybatisUserService mybatisUserService;

  @GetMapping
  public List<MybatisUser> getUsers() {
    return mybatisUserService.list();
  }

  @PostMapping
  public MybatisUser createUser(@RequestBody CreateUserModel model) {
    MybatisUser mybatisUser = new MybatisUser();
    mybatisUser.setName(model.getName());
    mybatisUserService.save(mybatisUser);
    return mybatisUser;
  }
}
