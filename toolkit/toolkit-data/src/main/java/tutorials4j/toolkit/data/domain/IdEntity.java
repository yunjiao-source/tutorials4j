package tutorials4j.toolkit.data.domain;

import java.io.Serializable;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface IdEntity<ID extends Serializable> extends Entity {
  ID getId();

  void setId(ID pk);

  default boolean isNew() {
    return getId() == null;
  }
}
