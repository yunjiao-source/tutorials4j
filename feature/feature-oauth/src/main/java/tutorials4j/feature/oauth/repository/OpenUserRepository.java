package tutorials4j.feature.oauth.repository;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import tutorials4j.feature.oauth.entity.OpenUserEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Repository
public interface OpenUserRepository extends BaseRepository<OpenUserEntity, String> {
  Optional<OpenUserEntity> findByUserIdAndClientId(String userId, String clientId);

  Optional<OpenUserEntity> findByUserIdAndSubjectId(String userId, String subjectId);
}
