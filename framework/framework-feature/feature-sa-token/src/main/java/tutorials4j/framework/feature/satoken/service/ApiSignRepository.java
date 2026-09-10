package tutorials4j.framework.feature.satoken.service;

import org.springframework.stereotype.Repository;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;
import tutorials4j.framework.feature.satoken.model.ApiSignEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Repository
public interface ApiSignRepository extends BaseRepository<ApiSignEntity, String> {}
