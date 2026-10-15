package tutorials4j.feature.upms.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tutorials4j.feature.upms.entity.PermissionEntity;
import tutorials4j.feature.upms.repository.PermissionRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseService;

/**
 * TODO
 *
 * @author YunJiao Yang
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
