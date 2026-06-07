package tutorials4j.framework.data.hibernate.domain;

import tutorials4j.framework.common.core.entity.DataStatusEnum;
import tutorials4j.framework.data.hibernate.convert.AbstractBaseEnumAttributeConverter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class DataStatusAttributeConverter
    extends AbstractBaseEnumAttributeConverter<DataStatusEnum, Integer> {

  protected DataStatusAttributeConverter() {
    super(DataStatusEnum.class);
  }
}
