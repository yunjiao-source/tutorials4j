package tutorials4j.toolkit.data.hibernate.domain;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tutorials4j.toolkit.data.domain.Entity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface QueryRepository<E extends Entity> {
  List<E> findAll(BaseQuery<E> query);

  long count(BaseQuery<E> query);

  Page<E> findAll(BaseQuery<E> query, Pageable pageable);
}
