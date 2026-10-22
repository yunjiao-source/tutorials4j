package tutorials4j.toolkit.data.hibernate.domain;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import tutorials4j.toolkit.data.domain.Entity;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@NoRepositoryBean
public interface BaseRepository<E extends Entity, ID extends Serializable>
    extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {
  int MAX_BASE_QUERY_COUNT = 4000;

  default Page<E> findAll(BaseQuery<E> query, Pageable pageable) {
    return findAll(query.buildSpecification(), pageable);
  }

  default List<E> findAll(BaseQuery<E> query) {
    Pageable pageable = PageRequest.of(0, MAX_BASE_QUERY_COUNT);
    Page<E> page = findAll(query, pageable);
    return page.getContent();
  }
}
