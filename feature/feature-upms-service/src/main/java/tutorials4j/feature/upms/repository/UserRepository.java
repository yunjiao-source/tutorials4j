package tutorials4j.feature.upms.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tutorials4j.feature.upms.entity.UserEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Repository
public interface UserRepository extends BaseRepository<UserEntity, String> {

  @Override
  @EntityGraph(attributePaths = {"organize"})
  Page<UserEntity> findAll(Specification<UserEntity> spec, Pageable pageable);

  @Query(
      "select distinct t1 from UserEntity t1 left join fetch t1.roles where t1.username = :username")
  Optional<UserEntity> findWithRolesByUsername(@Param("username") String username);

  Optional<UserEntity> findByUsername(String username);

  long countByUsername(String key);
}
