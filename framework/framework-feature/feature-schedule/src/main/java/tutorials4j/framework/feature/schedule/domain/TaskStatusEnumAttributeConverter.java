package tutorials4j.framework.feature.schedule.domain;

import tutorials4j.framework.data.hibernate.convert.AbstractBaseEnumAttributeConverter;
import tutorials4j.framework.schedule.core.bean.TaskStatusEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class TaskStatusEnumAttributeConverter
    extends AbstractBaseEnumAttributeConverter<TaskStatusEnum, Integer> {

  protected TaskStatusEnumAttributeConverter() {
    super(TaskStatusEnum.class);
  }
}
