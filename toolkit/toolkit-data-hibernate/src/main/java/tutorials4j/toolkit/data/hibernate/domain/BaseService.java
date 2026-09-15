package tutorials4j.toolkit.data.hibernate.domain;

import java.io.Serializable;
import tutorials4j.toolkit.data.domain.Entity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface BaseService<E extends Entity, ID extends Serializable>
    extends WriteableService<E, ID> {}
