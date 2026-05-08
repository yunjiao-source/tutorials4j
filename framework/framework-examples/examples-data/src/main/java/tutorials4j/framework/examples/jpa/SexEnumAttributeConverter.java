package tutorials4j.framework.examples.jpa;

import tutorials4j.framework.data.hibernate.AbstractBaseEnumAttributeConverter;
import tutorials4j.framework.examples.SexEnum;

/**
 * 实现
 *
 * @author Yun Jiao
 */
public class SexEnumAttributeConverter extends AbstractBaseEnumAttributeConverter<SexEnum, String> {
    public SexEnumAttributeConverter() {
        super(SexEnum.class);
    }
}
