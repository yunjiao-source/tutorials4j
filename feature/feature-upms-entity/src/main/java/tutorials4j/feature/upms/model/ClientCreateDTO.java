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
public class ClientCreateDTO extends ClientUpdateDTO {
  private String clientId;

  private YesNoEnum reserved;
}
