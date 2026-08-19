package tutorials4j.framework.feature.schedule.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tutorials4j.framework.common.core.bean.YesNoEnum;
import tutorials4j.framework.data.hibernate.domain.BaseIdEntity;
import tutorials4j.framework.data.hibernate.domain.YesNoEnumAttributeConverter;
import tutorials4j.framework.schedule.spring.bean.TaskStatusEnum;

/**
 * 任务日志实体，对应数据表 feat_job_log。
 *
 * <p>记录任务每次运行的批次号、状态、统计结果与错误信息，并与任务（{@link JobEntity}） 构成多对一关系。
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(name = "feat_job_log")
@EqualsAndHashCode(callSuper = false)
@NamedEntityGraph(name = "JobLogEntity.withJob", attributeNodes = @NamedAttributeNode("job"))
public class JobLogEntity extends BaseIdEntity {
  /** 任务运行状态 */
  @Convert(converter = TaskStatusEnumAttributeConverter.class)
  private TaskStatusEnum taskStatus;

  /** 批次号 */
  @Column(length = 64)
  private String lotNo;

  /** 总处理数量 */
  private Integer totalCount;

  /** 失败数量 */
  private Integer totalFailureCount;

  /** 开始时间 */
  private Instant startTime;

  /** 结束时间 */
  private Instant endTime;

  /** 是否发生错误 */
  @Convert(converter = YesNoEnumAttributeConverter.class)
  private YesNoEnum hasError;

  /** 日志消息（最长 500 字符） */
  @Column(length = 500)
  private String message;

  /** 创建时间，持久化时自动填充 */
  @Column(updatable = false)
  private Instant createdAt;

  /** 关联的任务 */
  @ManyToOne
  @JoinColumn(name = "job_id", nullable = false)
  private JobEntity job;

  /** 持久化前填充创建时间。 */
  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
  }
}
