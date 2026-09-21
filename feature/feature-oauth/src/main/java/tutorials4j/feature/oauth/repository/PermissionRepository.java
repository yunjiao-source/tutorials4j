package tutorials4j.feature.oauth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tutorials4j.feature.oauth.entity.EntityGraphConsts;
import tutorials4j.feature.oauth.entity.PermissionEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Repository
public interface PermissionRepository extends BaseRepository<PermissionEntity, String> {
  @EntityGraph(
      value = EntityGraphConsts.PERMISSION_WITH_ROLE,
      type = EntityGraph.EntityGraphType.FETCH)
  @Query("select e from PermissionEntity e where e.id = :id")
  Optional<PermissionEntity> findWithRolesById(@Param("id") String id);

  Optional<PermissionEntity> findByCode(String code);
}
