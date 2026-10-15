package tutorials4j.feature.upms.repository;

import static tutorials4j.feature.upms.UpmsFeatureConsts.CACHE_NAME_CLIENTS;

import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import tutorials4j.feature.upms.entity.ClientEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Repository
public interface ClientRepository extends BaseRepository<ClientEntity, String> {
  @Cacheable(value = CACHE_NAME_CLIENTS, key = "#clientId")
  Optional<ClientEntity> findByClientId(String clientId);
}
