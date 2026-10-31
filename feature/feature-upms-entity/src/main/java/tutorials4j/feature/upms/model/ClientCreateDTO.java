package tutorials4j.feature.upms.model;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
public class ClientCreateDTO extends ClientUpdateDTO {
  private String clientId;

  private String clientSecret;
}
