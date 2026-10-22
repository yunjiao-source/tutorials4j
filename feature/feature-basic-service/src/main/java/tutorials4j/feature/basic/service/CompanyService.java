package tutorials4j.feature.basic.service;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import tutorials4j.feature.basic.entity.CompanyEntity;
import tutorials4j.feature.basic.model.CompanyDTO;
import tutorials4j.feature.basic.repository.CompanyRepository;
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
public class CompanyService implements BaseService<CompanyEntity, String> {
  private final CompanyRepository companyRepository;

  @Override
  public BaseRepository<CompanyEntity, String> getRepository() {
    return companyRepository;
  }

  public CompanyEntity create(@Valid CompanyDTO dto) {
    Assert.notNull(dto, "dto must not be null");
    CompanyEntity entity = new CompanyEntity();
    BeanUtils.copyProperties(dto, entity);

    return companyRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public CompanyEntity update(String id, @Valid CompanyDTO dto) {
    Assert.notNull(dto, "dto must not be null");

    CompanyEntity entity = findById(id);
    BeanUtils.copyProperties(dto, entity);
    return companyRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    companyRepository.findById(id).ifPresent(companyRepository::delete);
  }
}
