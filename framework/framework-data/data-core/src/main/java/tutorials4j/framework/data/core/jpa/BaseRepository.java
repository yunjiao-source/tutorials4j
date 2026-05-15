package tutorials4j.framework.data.core.jpa;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@NoRepositoryBean
public interface BaseRepository<E extends BaseEntity, ID extends Serializable>
    extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {}
