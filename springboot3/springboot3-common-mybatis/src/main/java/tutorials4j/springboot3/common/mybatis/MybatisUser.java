package tutorials4j.springboot3.common.mybatis;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用戶
 *
 * @author Yun Jiao
 */
@Data
@TableName("user_mybatis_tenant")
public class MybatisUser {

  @TableId private Long id;
  private String name;
}
