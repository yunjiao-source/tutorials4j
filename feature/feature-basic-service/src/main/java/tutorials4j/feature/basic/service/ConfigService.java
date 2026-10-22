package tutorials4j.feature.basic.service;

import static tutorials4j.feature.basic.BasicFeatureConsts.CACHEABLE_CONFIG;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import tutorials4j.feature.basic.entity.ConfigEntity;
import tutorials4j.feature.basic.model.ConfigCreateDTO;
import tutorials4j.feature.basic.model.ConfigUpdateDTO;
import tutorials4j.feature.basic.repository.ConfigRepository;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Service
@Validated
@RequiredArgsConstructor
public class ConfigService implements BaseService<ConfigEntity, String> {
  private final ConfigRepository configRepository;

  @Override
  public BaseRepository<ConfigEntity, String> getRepository() {
    return configRepository;
  }

  public List<ConfigEntity> findByCategory(String category, Set<YesNoEnum> allEnabled) {
    Assert.notNull(category, "category must not be null");

    ConfigQuery query = ConfigQuery.builder().category(category).allEnabled(allEnabled).build();
    return findByQuery(query);
  }

  public ConfigEntity create(@Valid ConfigCreateDTO dto) {
    Assert.notNull(dto, "dto must not be null");
    ConfigEntity entity = new ConfigEntity();
    BeanUtils.copyProperties(dto, entity);

    return configRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public ConfigEntity update(String id, @Valid ConfigUpdateDTO dto) {
    Assert.notNull(dto, "model must not be null");

    ConfigEntity entity = findById(id);
    BeanUtils.copyProperties(dto, entity);
    return configRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    configRepository.findById(id).ifPresent(configRepository::delete);
  }

  @Cacheable(value = CACHEABLE_CONFIG, key = "#category")
  public List<ConfigEntity> findValidByCategory(String category) {
    Assert.notNull(category, "category must not be null");
    return findByCategory(category, Set.of(YesNoEnum.yes));
  }
}
