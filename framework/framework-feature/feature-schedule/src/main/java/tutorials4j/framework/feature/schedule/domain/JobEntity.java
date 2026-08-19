package tutorials4j.framework.feature.schedule.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tutorials4j.framework.common.core.bean.DataStatusEnum;
import tutorials4j.framework.data.hibernate.domain.BaseStatusEntity;
import tutorials4j.framework.schedule.core.bean.Task;

/**
 * 调度任务实体，对应数据表 feat_job。
 *
 * <p>实现 {@link Task} 接口，保存任务的编码、执行类、Cron 表达式等配置信息， 并与任务日志（{@link JobLogEntity}）构成一对多关系。
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(name = "feat_job")
@EqualsAndHashCode(callSuper = false)
public class JobEntity extends BaseStatusEntity implements Task {
  /** 任务编码，唯一且不可更新 */
  @Column(length = 64, unique = true, updatable = false)
  private String taskCode;

  /** 任务执行类的简单类名 */
  @Column(length = 64)
  private String classSimpleName;

  /** 任务 Cron 表达式 */
  @Column(length = 64)
  private String cron;

  /** 任务描述 */
  @Column(length = 254)
  private String description;

  /** 任务元数据（以 JSON 格式存储） */
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "json")
  private Map<String, String> metadata;

  /** 首次执行延迟 */
  private Duration initialDelay;

  /** 最大失败次数 */
  private Integer maxFailureCount;

  /** 最大执行次数 */
  private Integer maxExecutionCount;

  /** 任务到期时间 */
  private Instant dueDate;

  /** 任务的执行日志列表 */
  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JobLogEntity> logs = new ArrayList<>();

  /**
   * 根据启用标记设置任务的数据状态。
   *
   * @param enabled 是否启用
   */
  @Override
  public void setEnabled(boolean enabled) {
    if (enabled) {
      this.setDataStatus(DataStatusEnum.NORMAL);
    } else {
      this.setDataStatus(DataStatusEnum.DISABLED);
    }
  }

  /**
   * 判断任务当前是否处于启用状态。
   *
   * @return true 表示已启用
   */
  @Override
  public boolean isEnabled() {
    return Objects.equals(getDataStatus(), DataStatusEnum.NORMAL)
        || Objects.equals(getDataStatus(), DataStatusEnum.RESERVED);
  }
}
