package tutorials4j.toolkit.data.hibernate.generator;

import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.GeneratorCreationContext;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import tutorials4j.toolkit.core.util.SnowflakeUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class SnowflakeIdentifierGenerator implements IdentifierGenerator {
  private Class<?> javaIdType;

  @Override
  public void configure(GeneratorCreationContext creationContext, Properties parameters) {
    PersistentClass entity = creationContext.getPersistentClass();

    // 主键属性
    Property idProperty = entity.getIdentifierProperty();

    // Hibernate Type
    org.hibernate.type.Type idType = idProperty.getType();

    // Java 类型
    javaIdType = idProperty.getType().getReturnedClass();

    if (!Long.class.equals(javaIdType) && !String.class.equals(javaIdType)) {
      throw new HibernateException(
          "雪花算法 ID 生成器仅支持 Long 或 String 类型的主键字段，当前类型为：" + idType.getName());
    }
  }

  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object owner) {
    if (String.class.equals(javaIdType)) {
      return SnowflakeUtils.nextIdStr();
    }
    return SnowflakeUtils.nextId();
  }
}
