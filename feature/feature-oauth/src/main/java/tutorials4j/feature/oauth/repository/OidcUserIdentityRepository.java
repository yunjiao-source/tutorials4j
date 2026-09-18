package tutorials4j.feature.oauth.repository;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import tutorials4j.feature.oauth.entity.OidcUserIdentityEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Repository
public interface OidcUserIdentityRepository extends BaseRepository<OidcUserIdentityEntity, String> {
  Optional<OidcUserIdentityEntity> findByUserIdAndClientId(String userId, String clientId);

  Optional<OidcUserIdentityEntity> findByUserIdAndSubjectId(String userId, String subjectId);
}
