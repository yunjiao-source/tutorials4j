package tutorials4j.framework.feature.schedule.domain;

import tutorials4j.framework.data.hibernate.convert.AbstractBaseEnumAttributeConverter;
import tutorials4j.framework.schedule.spring.bean.TaskStatusEnum;

/**
 * 任务状态枚举的 JPA 属性转换器。
 *
 * <p>实现 {@link TaskStatusEnum} 与数据库整型编码之间的双向转换。
 *
 * @author Yun Jiao
 */
public class TaskStatusEnumAttributeConverter
    extends AbstractBaseEnumAttributeConverter<TaskStatusEnum, Integer> {

  /** 构造转换器并绑定 {@link TaskStatusEnum} 枚举类型。 */
  protected TaskStatusEnumAttributeConverter() {
    super(TaskStatusEnum.class);
  }
}
