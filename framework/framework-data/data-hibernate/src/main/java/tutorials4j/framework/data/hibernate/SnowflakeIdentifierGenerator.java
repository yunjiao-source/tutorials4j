package tutorials4j.framework.data.hibernate;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.id.factory.spi.StandardGenerator;
import tutorials4j.framework.common.core.util.SnowflakeUtils;

/**
 * Hibernate 主键生成器，基于雪花算法（Snowflake）生成分布式唯一 ID。
 *
 * <p>该类与 {@link SnowflakeIDGenerator} 注解配合使用，根据实体主键的类型自动返回字符串或数值型 ID。
 *
 * @author Yun Jiao
 * @see SnowflakeIDGenerator
 * @see SnowflakeUtils
 */
public class SnowflakeIdentifierGenerator implements IdentifierGenerator, StandardGenerator {
  private final Class<?> propertyType;

  public SnowflakeIdentifierGenerator(
      SnowflakeIDGenerator config,
      Member idMember,
      CustomIdGeneratorCreationContext creationContext) {
    // 初始化主键的类型
    if (idMember instanceof Method) {
      propertyType = ((Method) idMember).getReturnType();
    } else {
      propertyType = ((Field) idMember).getType();
    }
  }

  @Override
  public Object generate(SharedSessionContractImplementor session, Object object) {
    if (String.class.isAssignableFrom(propertyType)) {
      return SnowflakeUtils.nextIdStr();
    }
    return SnowflakeUtils.nextId();
  }
}
