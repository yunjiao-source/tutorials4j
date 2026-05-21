package tutorials4j.framework.data.hibernate.id;

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

  public UidentifierGenerator(
      UidGenerator config, Member idMember, CustomIdGeneratorCreationContext creationContext) {
    if (idMember instanceof Method) {
      propertyType = ((Method) idMember).getReturnType();
    } else {
      propertyType = ((Field) idMember).getType();
    }
  }

  @Override
  public Object generate(SharedSessionContractImplementor session, Object object) {
    if (String.class.isAssignableFrom(propertyType)) {
      return UidUtils.DEFAULTED.nextUidStr();
    }
    return UidUtils.DEFAULTED.nextUid();
  }
}
