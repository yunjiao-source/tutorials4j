package tutorials4j.framework.examples.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper接口
 *
 * @author Yun Jiao
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
  // 如有复杂查询SQL，可在此编写
}
