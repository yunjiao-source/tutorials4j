package tutorials4j.framework.assy.schedule;

import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class TaskDefinition {
  private String taskId; // 唯一标识
  private String name; // 任务名称
  private String cron; // Cron 表达式
  private Boolean enabled; // 是否启用
  private String taskType; // "BEAN_METHOD" 或 "RUNNABLE_CLASS"
  private String taskData; // 例如 "userService:sendEmail" 或 "com.example.MyTask"

  // getters/setters 省略
}
