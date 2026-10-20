package tutorials4j.toolkit.data.domain;

import java.io.Serializable;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class BaseIdVO<ID extends Serializable> {
  private ID id;
}
