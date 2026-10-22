package tutorials4j.feature.upms.service;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import tutorials4j.feature.upms.UpmsFeatureManager;
import tutorials4j.feature.upms.entity.ClientEntity;
import tutorials4j.feature.upms.exception.UpmsFeatureErrorCode;
import tutorials4j.feature.upms.model.ClientCreateDTO;
import tutorials4j.feature.upms.model.ClientUpdateDTO;
import tutorials4j.feature.upms.repository.ClientRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseService;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Service
@Validated
@RequiredArgsConstructor
public class ClientService implements BaseService<ClientEntity, String> {
  private final ClientRepository clientRepository;

  @Override
  public BaseRepository<ClientEntity, String> getRepository() {
    return clientRepository;
  }

  public ClientEntity create(@Valid ClientCreateDTO dto) {
    Assert.notNull(dto, "dto must not be null");
    ClientEntity entity = new ClientEntity();
    BeanUtils.copyProperties(dto, entity);
    UpmsFeatureManager.getInstance().setClientEntityDefaultValue.accept(entity);
    return clientRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public ClientEntity update(String id, @Valid ClientUpdateDTO dto) {
    Assert.notNull(dto, "model must not be null");

    ClientEntity entity = findById(id);
    BeanUtils.copyProperties(dto, entity);
    UpmsFeatureManager.getInstance().setClientEntityDefaultValue.accept(entity);
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
            () -> UpmsFeatureErrorCode.CLIENT_NOT_FOUND.throwed().param("clientId", clientId));
  }
}
