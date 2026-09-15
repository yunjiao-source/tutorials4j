package tutorials4j.toolkit.data.hibernate.domain;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import tutorials4j.toolkit.data.domain.Entity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@NoRepositoryBean
public interface BaseRepository<E extends Entity, ID extends Serializable>
    extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {}
