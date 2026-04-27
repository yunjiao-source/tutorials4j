package tutorials4j.framework.data.hibernate;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.id.factory.spi.StandardGenerator;
import tutorials4j.framework.common.core.util.SnowflakeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class SnowflakeIdentifierGenerator implements IdentifierGenerator, StandardGenerator {
    private final Class<?> propertyType;

    public SnowflakeIdentifierGenerator(SnowflakeIDGenerator config, Member idMember, CustomIdGeneratorCreationContext creationContext) {
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
