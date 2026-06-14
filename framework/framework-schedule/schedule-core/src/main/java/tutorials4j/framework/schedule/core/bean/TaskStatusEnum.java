package tutorials4j.framework.schedule.core.bean;

import tutorials4j.framework.common.core.bean.BaseEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public enum TaskStatusEnum implements BaseEnum<Integer> {
  CREATED(0, "创建"),
  STARTED(1, "启动"),
  COMPLETED(2, "完成"),
  STOPPED(3, "停止"),
  CANCELLED(4, "取消"),
  EXCEPTION(5, "异常");

  private final int code;
  private final String label;

  TaskStatusEnum(int code, String label) {
    this.code = code;
    this.label = label;
  }

  @Override
  public Integer getCode() {
    return code;
  }

  @Override
  public String getName() {
    return name();
  }

  @Override
  public String getLabel() {
    return label;
  }
}
