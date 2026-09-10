package tutorials4j.framework.feature.satoken.service;

import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;
import tutorials4j.framework.data.hibernate.domain.BaseService;
import tutorials4j.framework.feature.satoken.model.ApiSignEntity;
import tutorials4j.framework.feature.satoken.model.ApiSignModel;
import tutorials4j.framework.feature.satoken.model.ApiSignQuery;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class ApiSignService implements BaseService<ApiSignEntity, String> {
  private final ApiSignRepository apiSignRepository;

  @Override
  public BaseRepository<ApiSignEntity, String> getRepository() {
    return apiSignRepository;
  }

  public Page<ApiSignEntity> findByPage(ApiSignQuery query, Pageable pageable) {
    return apiSignRepository.findAll(query.buildSpecification(), pageable);
  }

  public ApiSignEntity create(ApiSignModel model) {
    ApiSignEntity entity = new ApiSignEntity();
    model.fillTo(entity);
    entity.setSecretKey(IdUtil.fastSimpleUUID());
    return apiSignRepository.save(entity);
  }

  public ApiSignEntity update(String id, ApiSignModel model) {
    ApiSignEntity entity = this.findById(id);
    model.fillTo(entity);
    return apiSignRepository.save(entity);
  }

  public ApiSignEntity delete(String id) {
    ApiSignEntity entity = this.findById(id);
    apiSignRepository.deleteById(id);
    return entity;
  }
}
