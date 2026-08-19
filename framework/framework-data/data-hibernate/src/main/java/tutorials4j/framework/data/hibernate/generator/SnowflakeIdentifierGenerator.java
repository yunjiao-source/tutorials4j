package tutorials4j.framework.data.hibernate.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.id.factory.spi.StandardGenerator;
import tutorials4j.framework.common.spring.util.SnowflakeUtils;

/**
 * Hibernate 主键生成器，基于雪花算法（Snowflake）生成分布式唯一 ID。
 *
 * <p>该类与 {@link SnowflakeIdGenerator} 注解配合使用，根据实体主键的类型自动返回字符串或数值型 ID。
 *
 * @author Yun Jiao
 * @see SnowflakeIdGenerator
 * @see SnowflakeUtils
 */
public class SnowflakeIdentifierGenerator implements IdentifierGenerator, StandardGenerator {
  private final Class<?> propertyType;

  /**
   * 根据注解配置与主键成员构造生成器。
   *
   * <p>通过反射确定主键属性类型，用于运行时决定返回字符串还是数值型 ID。
   *
   * @param config 主键生成器注解配置
   * @param idMember 主键属性对应的字段或 getter 方法
   * @param creationContext Hibernate 自定义生成器创建上下文
   */
  public SnowflakeIdentifierGenerator(
      SnowflakeIdGenerator config,
      Member idMember,
      CustomIdGeneratorCreationContext creationContext) {
    // 初始化主键的类型
    if (idMember instanceof Method) {
      propertyType = ((Method) idMember).getReturnType();
    } else {
      propertyType = ((Field) idMember).getType();
    }
  }

  /**
   * 根据主键类型生成雪花 ID：字符串类型返回 19 位十进制字符串，数值类型返回 {@code long} 值。
   *
   * @param session 当前 Hibernate 会话
   * @param object 待持久化的实体对象
   * @return 生成的分布式唯一 ID
   */
  @Override
  public Object generate(SharedSessionContractImplementor session, Object object) {
    if (String.class.isAssignableFrom(propertyType)) {
      return SnowflakeUtils.nextIdStr();
    }
    return SnowflakeUtils.nextId();
  }
}
