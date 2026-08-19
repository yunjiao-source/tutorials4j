package tutorials4j.framework.schedule.spring.bean;

import tutorials4j.framework.common.core.bean.BaseEnum;

/**
 * 任务状态枚举。
 *
 * <p>定义任务从创建到结束的各个状态，实现 {@link BaseEnum} 接口以提供统一的枚举访问方式。
 *
 * @author Yun Jiao
 */
public enum TaskStatusEnum implements BaseEnum<Integer> {
  /** 已创建。 */
  CREATED(0, "创建"),
  /** 已启动。 */
  STARTED(1, "启动"),
  /** 已完成。 */
  COMPLETED(2, "完成"),
  /** 已停止。 */
  STOPPED(3, "停止"),
  /** 已取消。 */
  CANCELLED(4, "取消"),
  /** 执行异常。 */
  EXCEPTION(5, "异常");

  /** 状态码。 */
  private final int code;

  /** 状态描述。 */
  private final String label;

  TaskStatusEnum(int code, String label) {
    this.code = code;
    this.label = label;
  }

  /** 返回状态码。 */
  @Override
  public Integer getCode() {
    return code;
  }

  /** 返回枚举名称。 */
  @Override
  public String getName() {
    return name();
  }

  /** 返回状态描述。 */
  @Override
  public String getLabel() {
    return label;
  }
}
