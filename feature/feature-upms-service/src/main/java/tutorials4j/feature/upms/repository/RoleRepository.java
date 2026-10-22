package tutorials4j.feature.upms.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tutorials4j.feature.upms.entity.RoleEntity;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Repository
public interface RoleRepository extends BaseRepository<RoleEntity, String> {
  @Query("select distinct t1 from RoleEntity t1 left join fetch t1.users where t1.id = :id")
  Optional<RoleEntity> findWithUsersById(@Param("id") String id);

  @Query(
      "select distinct t1 from RoleEntity t1 left join fetch t1.permissions where t1.code = :code")
  Optional<RoleEntity> findWithPermissionByCodeAAndEnabled(
      @Param("code") String code, @Param("enabled") YesNoEnum enabled);

  Optional<RoleEntity> findByCode(String code);

  long countByCode(String code);

  Set<RoleEntity> findByCodeIn(Collection<String> codes);
}
