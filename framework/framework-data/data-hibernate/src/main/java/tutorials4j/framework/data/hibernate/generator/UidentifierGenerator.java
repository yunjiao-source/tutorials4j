package tutorials4j.framework.data.hibernate.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.id.factory.spi.StandardGenerator;
import tutorials4j.framework.common.uid.UidUtils;

/**
 * Hibernate 主键生成器具体实现，支持 {@code Long} 和 {@code String} 类型主键。
 *
 * <p>构造时通过反射获取标识符属性的类型，运行时根据类型决定调用 {@link UidUtils#DEFAULTED} 的 {@code nextUid()} 或 {@code
 * nextUidStr()}。
 *
 * @author Yun Jiao
 * @see UidGenerator
 * @see UidUtils
 */
public class UidentifierGenerator implements IdentifierGenerator, StandardGenerator {
  private final Class<?> propertyType;

  /**
   * 根据注解配置与主键成员构造生成器。
   *
   * @param config 主键生成器注解配置
   * @param idMember 主键属性对应的字段或 getter 方法
   * @param creationContext Hibernate 自定义生成器创建上下文
   */
  public UidentifierGenerator(
      UidGenerator config, Member idMember, CustomIdGeneratorCreationContext creationContext) {
    if (idMember instanceof Method) {
      propertyType = ((Method) idMember).getReturnType();
    } else {
      propertyType = ((Field) idMember).getType();
    }
  }

  /**
   * 根据主键类型生成全局唯一 ID：字符串类型返回字符串，数值类型返回 {@code long} 值。
   *
   * @param session 当前 Hibernate 会话
   * @param object 待持久化的实体对象
   * @return 生成的全局唯一 ID
   */
  @Override
  public Object generate(SharedSessionContractImplementor session, Object object) {
    if (String.class.isAssignableFrom(propertyType)) {
      return UidUtils.DEFAULTED.nextUidStr();
    }
    return UidUtils.DEFAULTED.nextUid();
  }
}
