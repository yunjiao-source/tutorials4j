package tutorials4j.springboot3.common.mybatis;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 服务
 *
 * @author Yun Jiao
 */
@Service
public class MybatisUserService extends ServiceImpl<UserMapper, MybatisUser> {}
