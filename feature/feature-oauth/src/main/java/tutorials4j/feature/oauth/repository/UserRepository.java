package tutorials4j.feature.oauth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import tutorials4j.feature.oauth.entity.EntityGraphConsts;
import tutorials4j.feature.oauth.entity.UserEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Repository
public interface UserRepository extends BaseRepository<UserEntity, String> {
  @EntityGraph(
      value = EntityGraphConsts.USER_WITH_ROLE_PERMISSION,
      type = EntityGraph.EntityGraphType.FETCH)
  Optional<UserEntity> findWithRolesAndPermissionsByUsername(String username);

  @EntityGraph(value = EntityGraphConsts.USER_WITH_ROLE, type = EntityGraph.EntityGraphType.FETCH)
  Optional<UserEntity> findWithRolesByUsername(String username);

  Optional<UserEntity> findByUsername(String username);
}
