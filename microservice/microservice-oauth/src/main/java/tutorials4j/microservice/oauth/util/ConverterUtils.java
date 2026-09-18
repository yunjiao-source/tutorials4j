package tutorials4j.microservice.oauth.util;

import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import java.util.function.Function;
import org.springframework.beans.BeanUtils;
import tutorials4j.feature.oauth.entity.ClientEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ConverterUtils {
  private ConverterUtils() {}

  private static class Holder {
    private static final ConverterUtils INSTANCE = new ConverterUtils();
  }

  public static ConverterUtils getInstance() {
    return ConverterUtils.Holder.INSTANCE;
  }

  public Function<ClientEntity, SaClientModel> convertClientEntity2Model =
      (clientEntity) -> {
        SaClientModel model = new SaClientModel();
        BeanUtils.copyProperties(clientEntity, model);
        return model;
      };
}
