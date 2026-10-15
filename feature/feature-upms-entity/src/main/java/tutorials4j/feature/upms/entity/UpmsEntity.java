package tutorials4j.feature.upms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.hibernate.domain.BaseEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@ToString(callSuper = true)
@MappedSuperclass
public class UpmsEntity extends BaseEntity<String> {
  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum enabled;

  @Override
  protected void prePersist() {
    if (enabled == null) {
      enabled = YesNoEnum.yes;
    }
  }
}
