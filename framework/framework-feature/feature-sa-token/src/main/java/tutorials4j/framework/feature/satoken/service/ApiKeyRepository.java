package tutorials4j.framework.feature.satoken.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;
import tutorials4j.framework.feature.satoken.model.ApiKeyEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Repository
public interface ApiKeyRepository extends BaseRepository<ApiKeyEntity, String> {
  List<ApiKeyEntity> findByNamespaceAndLoginId(String namespace, String loginId);

  Optional<ApiKeyEntity> findByNamespaceAndApiKeyAndLoginId(
      String namespace, String apiKey, String loginId);

  Optional<ApiKeyEntity> findByNamespaceAndApiKey(String namespace, String apiKey);

  Optional<ApiKeyEntity> findByApiKey(String apiKey);

  long deleteByNamespaceAndLoginId(String namespace, String loginId);
}
