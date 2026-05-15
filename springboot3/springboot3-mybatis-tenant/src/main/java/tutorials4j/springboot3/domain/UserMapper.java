package tutorials4j.springboot3.domain;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * mapper接口
 *
 * @author Yun Jiao
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {}
