package tutorials4j.framework.data.hibernate.domain;

import tutorials4j.framework.common.core.bean.DataStatusEnum;
import tutorials4j.framework.data.hibernate.convert.AbstractBaseEnumAttributeConverter;

/**
 * 数据状态枚举的 JPA 属性转换器，负责 {@link DataStatusEnum} 与数据库整数之间的相互转换。
 *
 * @author Yun Jiao
 */
public class DataStatusAttributeConverter
    extends AbstractBaseEnumAttributeConverter<DataStatusEnum, Integer> {

  /** 构造转换器并指定目标枚举类型。 */
  protected DataStatusAttributeConverter() {
    super(DataStatusEnum.class);
  }
}
