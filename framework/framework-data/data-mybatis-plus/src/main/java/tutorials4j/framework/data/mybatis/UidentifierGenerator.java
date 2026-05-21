package tutorials4j.framework.data.mybatis;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import tutorials4j.framework.common.uid.UidUtils;

/**
 * MyBatis-Plus 主键生成器实现，委托给框架统一的 UID 生成工具。
 *
 * <p>当实体类主键字段未指定特定生成器时，可配合 MyBatis-Plus 配置使用此生成器， 自动为 {@code Number} 类型主键生成全局唯一 ID（雪花算法）， 为 {@code
 * String} 类型主键生成字符串形式唯一 ID。
 *
 * @author Yun Jiao
 * @see IdentifierGenerator
 * @see UidUtils
 */
public class UidentifierGenerator implements IdentifierGenerator {

  @Override
  public Number nextId(Object entity) {
    return UidUtils.DEFAULTED.nextUid();
  }

  @Override
  public String nextUUID(Object entity) {
    return UidUtils.DEFAULTED.nextUidStr();
  }
}
