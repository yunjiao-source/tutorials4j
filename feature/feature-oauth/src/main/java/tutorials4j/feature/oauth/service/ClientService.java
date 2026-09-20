package tutorials4j.feature.oauth.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tutorials4j.feature.oauth.OAuthFeatureManager;
import tutorials4j.feature.oauth.entity.ClientEntity;
import tutorials4j.feature.oauth.exception.OAuthFeatureErrorCode;
import tutorials4j.feature.oauth.model.ClientCreateModel;
import tutorials4j.feature.oauth.model.ClientUpdateModel;
import tutorials4j.feature.oauth.repository.ClientRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Service
@RequiredArgsConstructor
public class ClientService implements BaseService<ClientEntity, String> {
  private final ClientRepository clientRepository;

  @Override
  public BaseRepository<ClientEntity, String> getRepository() {
    return clientRepository;
  }

  public Page<ClientEntity> findByPage(ClientQuery query, Pageable pageable) {
    return clientRepository.findAll(query.buildSpecification(), pageable);
  }

  public ClientEntity create(ClientCreateModel model) {
    Assert.notNull(model, "model must not be null");
    var entity = new ClientEntity();
    BeanUtils.copyProperties(model, entity);
    OAuthFeatureManager.getInstance().setClientEntityDefaultValue.accept(entity);
    return clientRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public ClientEntity update(String id, ClientUpdateModel model) {
    Assert.notNull(model, "model must not be null");

    var entity = findById(id);
    BeanUtils.copyProperties(model, entity);
    OAuthFeatureManager.getInstance().setClientEntityDefaultValue.accept(entity);
    return clientRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    clientRepository.findById(id).ifPresent(clientRepository::delete);
  }

  public ClientEntity findByClientId(String clientId) {
    return clientRepository
        .findByClientId(clientId)
        .orElseThrow(
            () -> OAuthFeatureErrorCode.CLIENT_NOT_FOUND.throwed().param("clientId", clientId));
  }
}
