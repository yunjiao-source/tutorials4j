package tutorials4j.framework.data.hibernate.domain;

import tutorials4j.framework.common.core.bean.YesNoEnum;
import tutorials4j.framework.data.hibernate.convert.AbstractBaseEnumAttributeConverter;

/**
 * 是否枚举的 JPA 属性转换器，负责 {@link YesNoEnum} 与数据库整数之间的相互转换。
 *
 * @author Yun Jiao
 */
public class YesNoEnumAttributeConverter
    extends AbstractBaseEnumAttributeConverter<YesNoEnum, Integer> {

  /** 构造转换器并指定目标枚举类型。 */
  protected YesNoEnumAttributeConverter() {
    super(YesNoEnum.class);
  }
}
