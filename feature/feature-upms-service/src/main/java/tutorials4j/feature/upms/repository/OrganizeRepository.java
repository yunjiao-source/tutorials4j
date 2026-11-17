package tutorials4j.feature.upms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import tutorials4j.feature.upms.entity.OrganizeEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Repository
public interface OrganizeRepository extends BaseRepository<OrganizeEntity, String> {
  @Override
  @EntityGraph(attributePaths = {"parent"})
  Page<OrganizeEntity> findAll(Specification<OrganizeEntity> spec, Pageable pageable);
}
