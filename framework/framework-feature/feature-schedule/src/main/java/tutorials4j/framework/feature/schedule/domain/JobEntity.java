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
import tutorials4j.framework.common.core.entity.DataStatusEnum;
import tutorials4j.framework.data.hibernate.domain.BaseStatusEntity;
import tutorials4j.framework.schedule.core.bean.Task;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(name = "feat_job")
@EqualsAndHashCode(callSuper = false)
public class JobEntity extends BaseStatusEntity implements Task {
  @Column(length = 64, unique = true, updatable = false)
  private String taskCode;

  @Column(length = 64)
  private String classSimpleName;

  @Column(length = 64)
  private String cron;

  @Column(length = 254)
  private String description;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "json")
  private Map<String, String> metadata;

  private Duration initialDelay;
  private Integer maxFailureCount;
  private Integer maxExecutionCount;
  private Instant dueDate;

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JobLogEntity> logs = new ArrayList<>();

  @Override
  public void setEnabled(boolean enabled) {
    if (enabled) {
      this.setDataStatus(DataStatusEnum.NORMAL);
    } else {
      this.setDataStatus(DataStatusEnum.DISABLED);
    }
  }

  @Override
  public boolean isEnabled() {
    return Objects.equals(getDataStatus(), DataStatusEnum.NORMAL)
        || Objects.equals(getDataStatus(), DataStatusEnum.RESERVED);
  }
}
