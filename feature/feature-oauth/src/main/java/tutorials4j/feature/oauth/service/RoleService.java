package tutorials4j.feature.oauth.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tutorials4j.feature.oauth.entity.RoleEntity;
import tutorials4j.feature.oauth.repository.RoleRepository;
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
public class RoleService implements BaseService<RoleEntity, String> {
  private final RoleRepository roleRepository;

  @Override
  public BaseRepository<RoleEntity, String> getRepository() {
    return roleRepository;
  }
}
