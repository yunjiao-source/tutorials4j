package tutorials4j.springboot3.task;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 提醒任务
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(name = "remind_task")
public class RemindTask {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  // 任务名称
  private String name;

  // cron表达式
  private String cron;

  // 提醒任务的实现类
  private String beanClazz;

  public RemindTask() {}

  public RemindTask(String name, String cron, String beanClazz) {
    this.name = name;
    this.cron = cron;
    this.beanClazz = beanClazz;
  }
}
