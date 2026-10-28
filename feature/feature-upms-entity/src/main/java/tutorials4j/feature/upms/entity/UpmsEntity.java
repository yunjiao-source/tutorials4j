package tutorials4j.feature.upms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.exception.DataErrorCode;
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

  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum reserved;

  @Override
  protected void prePersist() {
    if (enabled == null) {
      enabled = YesNoEnum.yes;
    }

    if (reserved == null) {
      reserved = YesNoEnum.no;
    }
  }

  @Override
  protected void preRemove() {
    super.preRemove();

    if (YesNoEnum.isTrue(reserved)) {
      throw DataErrorCode.RESERVED_DATA_CAN_NOT_REMOVE
          .throwed()
          .param("className", getClass().getName());
    }
  }
}
