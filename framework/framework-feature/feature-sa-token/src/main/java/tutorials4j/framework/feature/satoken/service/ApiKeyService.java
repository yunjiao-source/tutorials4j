package tutorials4j.framework.feature.satoken.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;
import tutorials4j.framework.data.hibernate.domain.BaseService;
import tutorials4j.framework.feature.satoken.model.ApiKeyEntity;
import tutorials4j.framework.feature.satoken.model.ApiKeyQuery;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class ApiKeyService implements BaseService<ApiKeyEntity, String> {
  private final ApiKeyRepository apiKeyRepository;

  @Override
  public BaseRepository<ApiKeyEntity, String> getRepository() {
    return apiKeyRepository;
  }

  public Page<ApiKeyEntity> findByPage(ApiKeyQuery query, Pageable pageable) {
    return apiKeyRepository.findAll(query.buildSpecification(), pageable);
  }
}
