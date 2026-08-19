package tutorials4j.framework.examples.mybatis.table;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口。
 *
 * <p>继承 MyBatis-Plus 的 {@link BaseMapper}，提供用户表的基础 CRUD 能力。
 *
 * @author Yun Jiao
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
  // 如有复杂查询SQL，可在此编写
}
