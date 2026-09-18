package tutorials4j.microservice.oauth.common;

import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class SaClientModelExt extends SaClientModel {
  private String clientName;
}
