package tutorials4j.feature.upms.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tutorials4j.feature.upms.entity.PermissionEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Repository
public interface PermissionRepository extends BaseRepository<PermissionEntity, String> {
  @Query("select distinct t1 from PermissionEntity t1 left join fetch t1.roles  where t1.id = :id")
  Optional<PermissionEntity> findWithRolesById(@Param("id") String id);

  Optional<PermissionEntity> findByCode(String code);
}
