package tutorials4j.feature.upms.repository;

import org.springframework.stereotype.Repository;
import tutorials4j.feature.upms.entity.MenuEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Repository
public interface MenuRepository extends BaseRepository<MenuEntity, String> {}
