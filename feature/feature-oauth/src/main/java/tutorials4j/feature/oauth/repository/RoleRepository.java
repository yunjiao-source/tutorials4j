package tutorials4j.feature.oauth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tutorials4j.feature.oauth.entity.EntityGraphConsts;
import tutorials4j.feature.oauth.entity.RoleEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Repository
public interface RoleRepository extends BaseRepository<RoleEntity, String> {
  @EntityGraph(value = EntityGraphConsts.ROLE_WITH_USER, type = EntityGraph.EntityGraphType.FETCH)
  @Query("select e from RoleEntity e where e.id = :id")
  Optional<RoleEntity> findWithUsersById(@Param("id") String id);

  @EntityGraph(
      value = EntityGraphConsts.ROLE_WITH_PERMISSION,
      type = EntityGraph.EntityGraphType.FETCH)
  @Query("select e from RoleEntity e where e.code = :code")
  Optional<RoleEntity> findWithPermissionByCode(@Param("code") String code);

  Optional<RoleEntity> findByCode(String code);
}
