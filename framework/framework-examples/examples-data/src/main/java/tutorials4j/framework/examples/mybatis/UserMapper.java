package tutorials4j.framework.examples.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 MyBatis-Plus Mapper 接口。
 *
 * <p>继承 {@link BaseMapper} 获得基础增删改查能力；如有复杂查询 SQL，可在本接口中编写。
 *
 * @author Yun Jiao
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
  // 如有复杂查询SQL，可在此编写
}
