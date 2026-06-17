package tutorials4j.framework.data.hibernate.domain;

import tutorials4j.framework.common.core.entity.YesNoEnum;
import tutorials4j.framework.data.hibernate.convert.AbstractBaseEnumAttributeConverter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class YesNoEnumAttributeConverter
    extends AbstractBaseEnumAttributeConverter<YesNoEnum, Integer> {

  protected YesNoEnumAttributeConverter() {
    super(YesNoEnum.class);
  }
}
