package tutorials4j.feature.oauth.repository;

import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import tutorials4j.feature.oauth.entity.ClientEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Repository
public interface ClientRepository extends BaseRepository<ClientEntity, String> {
  @Cacheable(value = "oauth-clients", key = "#clientId")
  Optional<ClientEntity> findByClientId(String clientId);
}
