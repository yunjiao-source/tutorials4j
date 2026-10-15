package tutorials4j.feature.upms.model;

import lombok.Getter;
import lombok.Setter;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
public class ClientCreateModel extends ClientUpdateModel {
  private String clientId;

  private YesNoEnum isDefault;
}
