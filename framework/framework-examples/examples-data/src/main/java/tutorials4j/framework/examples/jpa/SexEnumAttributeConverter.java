package tutorials4j.framework.examples.jpa;

import tutorials4j.framework.data.hibernate.convert.AbstractBaseEnumAttributeConverter;
import tutorials4j.framework.examples.SexEnum;

/**
 * 性别枚举的 JPA 属性转换器。
 *
 * <p>实现 {@link SexEnum} 与数据库字符串编码之间的双向转换。
 *
 * @author Yun Jiao
 */
public class SexEnumAttributeConverter extends AbstractBaseEnumAttributeConverter<SexEnum, String> {
  /** 构造转换器并绑定 {@link SexEnum} 枚举类型。 */
  public SexEnumAttributeConverter() {
    super(SexEnum.class);
  }
}
