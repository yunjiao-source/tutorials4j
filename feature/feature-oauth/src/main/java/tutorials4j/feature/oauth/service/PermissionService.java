package tutorials4j.feature.oauth.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tutorials4j.feature.oauth.entity.PermissionEntity;
import tutorials4j.feature.oauth.repository.PermissionRepository;
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
public class PermissionService implements BaseService<PermissionEntity, String> {
  private final PermissionRepository permissionRepository;

  @Override
  public BaseRepository<PermissionEntity, String> getRepository() {
    return permissionRepository;
  }
}
