package tutorials4j.feature.oauth.repository;

import java.util.Optional;
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
  Optional<ClientEntity> findByClientId(String clientId);
}
