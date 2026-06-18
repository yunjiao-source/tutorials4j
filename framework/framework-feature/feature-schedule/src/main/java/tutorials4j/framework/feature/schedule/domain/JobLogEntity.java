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
import tutorials4j.framework.common.core.entity.YesNoEnum;
import tutorials4j.framework.data.hibernate.domain.BaseIdEntity;
import tutorials4j.framework.data.hibernate.domain.YesNoEnumAttributeConverter;
import tutorials4j.framework.schedule.core.bean.TaskStatusEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(name = "feat_job_log")
@EqualsAndHashCode(callSuper = false)
@NamedEntityGraph(name = "JobLogEntity.withJob", attributeNodes = @NamedAttributeNode("job"))
public class JobLogEntity extends BaseIdEntity {
  @Convert(converter = TaskStatusEnumAttributeConverter.class)
  private TaskStatusEnum taskStatus;

  @Column(length = 64)
  private String lotNo;

  private Integer totalCount;
  private Integer totalFailureCount;
  private Instant startTime;
  private Instant endTime;

  @Convert(converter = YesNoEnumAttributeConverter.class)
  private YesNoEnum hasError;

  @Column(length = 500)
  private String message;

  @Column(updatable = false)
  private Instant createdAt;

  @ManyToOne
  @JoinColumn(name = "job_id", nullable = false)
  private JobEntity job;

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
  }
}
